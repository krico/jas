package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.dao.multipass.MultipassDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.ModelOperation;
import com.jasify.schedule.appengine.model.TransactionOperator;
import com.jasify.schedule.appengine.model.consistency.ConsistencyGuard;
import com.jasify.schedule.appengine.model.consistency.InconsistentModelStateException;
import com.jasify.schedule.appengine.model.multipass.Multipass;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.dm.JasAddMultipassRequest;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.BeanUtil;

import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpoint.*;

/**
 * @author wszarmach
 * @since 09/11/15.
 */
@Api(name = "jasify", /* WARN: Its LAME but you have to copy & paste this section to all *Endpoint classes in this package */
        version = "v1",
        defaultVersion = AnnotationBoolean.TRUE,
        description = "Jasify Schedule",
        authenticators = {JasifyAuthenticator.class},
        authLevel = AuthLevel.NONE,
        transformers = {
                /* one per line in alphabetical order to avoid merge conflicts */
                JasAccountTransformer.class,
                JasActivityPackageTransformer.class,
                JasActivityTransformer.class,
                JasActivityTypeTransformer.class,
                JasContactMessageTransformer.class,
                JasGroupTransformer.class,
                JasHistoryTransformer.class,
                JasKeyTransformer.class,
                JasMultipassTransformer.class,
                JasOrganizationTransformer.class,
                JasPaymentTransformer.class,
                JasRepeatDetailsTransformer.class,
                JasSubscriptionTransformer.class,
                JasTransactionTransformer.class,
                JasUserLoginTransformer.class,
                JasUserTransformer.class
        },
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class MultipassEndpoint {
    private final MultipassDao multipassDao = new MultipassDao();

    @ApiMethod(name = "multipasses.add", path = "multipasses", httpMethod = ApiMethod.HttpMethod.POST)
    public Multipass addMultipass(@SuppressWarnings("unused")User caller, JasAddMultipassRequest request) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        checkFound(request, "request == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromOrganizationId(request.getOrganizationId()));
        final Multipass multipass = checkFound(request.getMultipass(), "request.multipass == null");
        final Key organizationId = checkFound(request.getOrganizationId(), "request.organizationId == null");

        try {
            return TransactionOperator.execute(new ModelOperation<Multipass>() {
                @Override
                public Multipass execute(Transaction tx) throws ModelException {
                    multipassDao.save(multipass, organizationId);
                    tx.commit();
                    return multipass;
                }
            });
        } catch (ModelException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "multipasses.get", path = "multipasses/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public Multipass getMultipass(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        checkFound(id, "id == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromMultipassId(id));
        try {
            return multipassDao.get(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "multipasses.query", path = "multipasses", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Multipass> getMultipasses(@SuppressWarnings("unused") User caller, @Named("organizationId") Key organizationId) throws NotFoundException {
        checkFound(organizationId, "organizationId == null");
        return multipassDao.getByOrganization(organizationId);
    }

    @ApiMethod(name = "multipasses.remove", path = "multipasses", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeMultipass(User caller, @Named("id") final Key id) throws NotFoundException, UnauthorizedException, ForbiddenException, InternalServerErrorException, BadRequestException {
        checkFound(id, "id == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromMultipassId(id));

        try {
            ConsistencyGuard.beforeDelete(Multipass.class, id);

            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws EntityNotFoundException {
                    multipassDao.get(id); //Throws not found if this Multipass doesn't exist
                    multipassDao.delete(id);
                    tx.commit();
                    return null;
                }
            });
        } catch (InconsistentModelStateException e) {
            throw new BadRequestException(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (ModelException me) {
            throw new InternalServerErrorException(me.getMessage());
        }
    }

    @ApiMethod(name = "multipasses.update", path = "multipasses/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public Multipass updateMultipass(User caller, @Named("id") final Key id, final Multipass multipass) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        checkFound(id, "id == null");
        checkFound(multipass, "multipass == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromMultipassId(id));
        multipass.setId(id);

        try {
            return TransactionOperator.execute(new ModelOperation<Multipass>() {
                @Override
                public Multipass execute(Transaction tx) throws ModelException {
                    Multipass dbMultipass = multipassDao.get(id);
                    BeanUtil.copyPropertiesExcluding(dbMultipass, multipass, "created", "modified", "id", "organizationRef");
                    multipassDao.save(dbMultipass);
                    tx.commit();
                    return dbMultipass;
                }
            });
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (ModelException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
