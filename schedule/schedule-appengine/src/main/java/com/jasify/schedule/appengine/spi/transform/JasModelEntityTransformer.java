package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.ModelEntity;
import com.jasify.schedule.appengine.spi.dm.JasEndpointEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * @author krico
 * @since 09/01/15.
 */
public class JasModelEntityTransformer implements Transformer<ModelEntity, JasEndpointEntity> {
    private static final Logger log = LoggerFactory.getLogger(JasModelEntityTransformer.class);

    private final Transformer<ModelEntity, JasEndpointEntity> transformer;

    public JasModelEntityTransformer(Type type) {
        transformer = newTransformerFor(type);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private Transformer<ModelEntity, JasEndpointEntity> newTransformerFor(Type type) {
        Preconditions.checkArgument(type instanceof Class, "Only support Class, not: " + type);
        Class<?> modelClass = (Class) type;
        String transformerClass = getClass().getPackage().getName() + ".Jas" + modelClass.getSimpleName() + "Transformer";
        log.info("Deduced transformer class [{}] for [{}]", transformerClass, type);
        try {
            return Class.forName(transformerClass).asSubclass(Transformer.class).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to instantiate transformer [" + transformerClass + "] for type [" + type + "]", e);
        }
    }

    @SuppressWarnings("unchecked")
    <M extends ModelEntity, E extends JasEndpointEntity> Transformer<M, E> getTransformer() {
        return (Transformer<M, E>) transformer;
    }

    @Override
    public JasEndpointEntity transformTo(ModelEntity modelEntity) {
        return transformer.transformTo(modelEntity);
    }

    @Override
    public ModelEntity transformFrom(JasEndpointEntity jasEndpointEntity) {
        return transformer.transformFrom(jasEndpointEntity);
    }
}
