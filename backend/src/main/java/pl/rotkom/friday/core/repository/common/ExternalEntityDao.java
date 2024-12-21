package pl.rotkom.friday.core.repository.common;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import pl.rotkom.friday.core.repository.model.common.ExternalEntity;

public abstract class ExternalEntityDao<T extends ExternalEntity> implements IExternalEntityDao<T> {

    private final Class<T> entity;

    private final EntityManager entityManager;

    public ExternalEntityDao(Class<T> entity, EntityManager entityManager) {
        this.entity = entity;
        this.entityManager = entityManager;
    }

    @Override
    public Map<String, Long> getExternalIdToIdMapping() {
        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createTupleQuery();

        var root = query.from(entity);

        query.multiselect(root.get("externalId"), root.get("id"));

        return toMap(
                entityManager.createQuery(query),
                String.class,
                Long.class
        );
    }

    @Override
    public Map<Long, String> getIdToExternalIdMapping() {
        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createTupleQuery();

        var root = query.from(entity);

        query.multiselect(root.get("id"), root.get("externalId"));

        return toMap(
                entityManager.createQuery(query),
                Long.class,
                String.class
        );
    }

    @Override
    public T getById(Long id) {
        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createQuery(entity);

        var root = query.from(entity);
        query.select(root).where(cb.equal(root.get("id"), id));

        return uniqueResult(entityManager.createQuery(query));
    }

    @Override
    public void save(T entity) {
        entityManager.persist(entity);
    }

    @Override
    public List<T> getAll() {
        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createQuery(entity);

        var root = query.from(entity);
        query.select(root);

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<String> getExternalIds() {
        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createQuery(String.class);

        var root = query.from(entity);
        query.select(root.get("externalId"));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Long> getIds() {
        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);

        var root = query.from(entity);
        query.select(root.get("id"));

        return entityManager.createQuery(query).getResultList();
    }

    private T uniqueResult(TypedQuery<T> query) {
        var result = query.getResultList();
        return result.isEmpty() ? null : result.getFirst();
    }

    private <K, V> Map<K, V> toMap(TypedQuery<Tuple> query, Class<K> keyType, Class<V> keyValue) {
        return query.getResultList().stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, keyType), tuple -> tuple.get(1, keyValue)));
    }
}
