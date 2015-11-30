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
import com.jasify.schedule.appengine.spi.dm.JasMultipassRequest;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.BeanUtil;

import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpoint.checkFound;
import static com.jasify.schedule.appengine.spi.JasifyEndpoint.mustBeAdminOrOrgMember;

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
    public Multipass add(User caller, final JasMultipassRequest request) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        checkFound(request, "request == null");
        final Key organizationId = checkFound(request.getOrganizationId(), "request.organizationId == null");
        final Multipass multipass = checkFound(request.getMultipass(), "request.multipass == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromOrganizationId(organizationId));

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

    @ApiMethod(name = "multipasses.get", path = "multipasses/{multipassId}", httpMethod = ApiMethod.HttpMethod.GET)
    public Multipass get(User caller, @Named("multipassId") Key multipassId) throws NotFoundException, UnauthorizedException, ForbiddenException {
        checkFound(multipassId, "multipassId == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromMultipassId(multipassId));
        try {
            return multipassDao.get(multipassId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "multipasses.query", path = "multipasses", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Multipass> query(@SuppressWarnings("unused") User caller, @Named("organizationId") Key organizationId) throws NotFoundException {
        checkFound(organizationId, "organizationId == null");
        return multipassDao.getByOrganization(organizationId);
    }

    @ApiMethod(name = "multipasses.remove", path = "multipasses", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(User caller, @Named("multipassId") final Key multipassId) throws NotFoundException, UnauthorizedException, ForbiddenException, InternalServerErrorException, BadRequestException {
        checkFound(multipassId, "multipassId == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromMultipassId(multipassId));

        try {
            ConsistencyGuard.beforeDelete(Multipass.class, multipassId);

            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws EntityNotFoundException {
                    multipassDao.get(multipassId); //Throws not found if this Multipass doesn't exist
                    multipassDao.delete(multipassId);
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

    @ApiMethod(name = "multipasses.update", path = "multipasses/{multipassId}", httpMethod = ApiMethod.HttpMethod.PUT)
    public Multipass update(User caller, @Named("multipassId") final Key multipassId, final JasMultipassRequest request) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        checkFound(multipassId, "multipassid == null");
        checkFound(request, "request == null");
        final Multipass multipass = checkFound(request.getMultipass(), "request.multipass == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromMultipassId(multipassId));
        multipass.setId(multipassId);

        try {
            return TransactionOperator.execute(new ModelOperation<Multipass>() {
                @Override
                public Multipass execute(Transaction tx) throws ModelException {
                    Multipass dbMultipass = multipassDao.get(multipassId); //Throws not found if this Multipass doesn't exist
                    BeanUtil.copyPropertiesExcluding(dbMultipass, multipass, "created", "modified", "id", "organizationRef");
                    multipassDao.save(dbMultipass, dbMultipass.getOrganizationRef().getKey());
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
