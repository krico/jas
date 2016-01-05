package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.dao.message.ContactMessageDao;
import com.jasify.schedule.appengine.mail.MailServiceFactory;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.ModelOperation;
import com.jasify.schedule.appengine.model.TransactionOperator;
import com.jasify.schedule.appengine.model.message.ContactMessage;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.transform.*;

import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpoint.checkFound;
import static com.jasify.schedule.appengine.spi.JasifyEndpoint.mustBeAdmin;

/**
 * @author wszarmach
 * @since 30/10/15.
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
public class MessageEndpoint {
    private final ContactMessageDao contactMessageDao = new ContactMessageDao();

    @ApiMethod(name = "contactMessages.add", path = "contact-messages", httpMethod = ApiMethod.HttpMethod.POST)
    public void add(@SuppressWarnings("unused")User caller, ContactMessage message) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        try {
            contactMessageDao.save(message);
            StringBuilder text = new StringBuilder("From: ").append(message.getFirstName()).append(" ").append(message.getLastName()).append(" [").append(message.getEmail()).append("]\n");
            text.append("Subject: ").append(message.getSubject()).append("\n");
            text.append("Message:\n").append(message.getMessage());
            MailServiceFactory.getMailService().sendToApplicationOwners("[Contact Message Received]", null, text.toString());
        } catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "contactMessages.get", path = "contact-messages/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public ContactMessage get(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        checkFound(id, "id == null");
        mustBeAdmin(caller);
        try {
            return contactMessageDao.get(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "contactMessages.query", path = "contact-messages", httpMethod = ApiMethod.HttpMethod.GET)
    public List<ContactMessage> query(User caller) throws UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        return contactMessageDao.getAll();
    }

    @ApiMethod(name = "contactMessages.remove", path = "contact-messages", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(User caller, @Named("id") final Key id) throws NotFoundException, UnauthorizedException, ForbiddenException, InternalServerErrorException, BadRequestException {
        checkFound(id, "id == null");
        mustBeAdmin(caller);

        try {
            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws EntityNotFoundException {
                    contactMessageDao.get(id); //Throws not found if this ContactMessage doesn't exist
                    contactMessageDao.delete(id);
                    tx.commit();
                    return null;
                }
            });
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (ModelException me) {
            throw new InternalServerErrorException(me.getMessage());
        }
    }
}
