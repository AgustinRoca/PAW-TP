package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.daos.DoctorDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.generics.GenericDaoImpl;
import ar.edu.itba.paw.persistence.utils.StringSearchType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DoctorDaoImpl extends GenericDaoImpl<Doctor, Integer> implements DoctorDao {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String UNACCENT_FUNC = "unaccent";

    public DoctorDaoImpl() {
        super(Doctor.class, Doctor_.id);
    }

    @Override
    public List<Doctor> findBy(String name, String surname, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities) {
        Collection<String> names;
        Collection<String> surnames;
        if (name != null && !name.isEmpty()) {
            names = new LinkedList<>();
            names.add(name);
        } else {
            names = Collections.emptyList();
        }
        if (surname != null && !surname.isEmpty()) {
            surnames = new LinkedList<>();
            surnames.add(surname);
        } else {
            surnames = Collections.emptyList();
        }

        return this.findBy(names, surnames, offices, doctorSpecialties, localities);
    }

    @Override
    public List<Doctor> findBy(Collection<String> names, Collection<String> surnames, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities) {
        if (names == null) {
            names = Collections.emptyList();
        } else {
            names = names.stream().map(name -> StringUtils.stripAccents(name).toLowerCase()).collect(Collectors.toList());
        }
        if (surnames == null) {
            surnames = Collections.emptyList();
        } else {
            surnames = surnames.stream().map(surname -> StringUtils.stripAccents(surname).toLowerCase()).collect(Collectors.toList());
        }
        if (offices == null) {
            offices = Collections.emptyList();
        }
        if (doctorSpecialties == null) {
            doctorSpecialties = Collections.emptyList();
        }
        if (localities == null) {
            localities = Collections.emptyList();
        }

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Doctor> query = builder.createQuery(Doctor.class);
        Root<Doctor> root = query.from(Doctor.class);
        Join<Doctor, Office> officeJoin = root.join(Doctor_.office);
        Join<Doctor, User> userJoin = root.join(Doctor_.user);

        query.select(root);
        query.where(builder.and(this.getPredicates(
                names,
                surnames,
                offices,
                doctorSpecialties,
                localities,
                builder,
                root,
                officeJoin,
                userJoin
        )));

        return this.selectQuery(builder, query, root);
    }

    @Override
    public Paginator<Doctor> findBy(String name, String surname, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities, int page, int pageSize) {
        Collection<String> names;
        Collection<String> surnames;
        if (name != null && !name.isEmpty()) {
            names = new LinkedList<>();
            names.add(name);
        } else {
            names = Collections.emptyList();
        }
        if (surname != null && !surname.isEmpty()) {
            surnames = new LinkedList<>();
            surnames.add(surname);
        } else {
            surnames = Collections.emptyList();
        }

        return this.findBy(names, surnames, offices, doctorSpecialties, localities, page, pageSize);
    }

    @Override
    public Paginator<Doctor> findBy(Collection<String> names, Collection<String> surnames, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities, int page, int pageSize) {
        if (page <= 0) page = 1;
        if (pageSize <= 0) pageSize = DEFAULT_PAGE_SIZE;

        if (names == null) {
            names = Collections.emptyList();
        } else {
            names = names.stream().map(name -> StringUtils.stripAccents(name).toLowerCase()).collect(Collectors.toList());
        }
        if (surnames == null) {
            surnames = Collections.emptyList();
        } else {
            surnames = surnames.stream().map(surname -> StringUtils.stripAccents(surname).toLowerCase()).collect(Collectors.toList());
        }
        if (offices == null) {
            offices = Collections.emptyList();
        }
        if (doctorSpecialties == null) {
            doctorSpecialties = Collections.emptyList();
        }
        if (localities == null) {
            localities = Collections.emptyList();
        }

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Doctor> query = builder.createQuery(Doctor.class);
        Root<Doctor> root = query.from(Doctor.class);
        Join<Doctor, Office> officeJoin = root.join(Doctor_.office);
        Join<Doctor, User> userJoin = root.join(Doctor_.user);

        CriteriaQuery<Tuple> tupleQuery = builder.createQuery(Tuple.class);
        Root<Doctor> rootCount = tupleQuery.from(Doctor.class);
        Join<Doctor, Office> officeJoinCount = rootCount.join(Doctor_.office);
        Join<Doctor, User> userJoinCount = rootCount.join(Doctor_.user);

        query.select(root);
        query.where(builder.and(this.getPredicates(
                names,
                surnames,
                offices,
                doctorSpecialties,
                localities,
                builder,
                root,
                officeJoin,
                userJoin
        )));

        tupleQuery.where(builder.and(this.getPredicates(
                names,
                surnames,
                offices,
                doctorSpecialties,
                localities,
                builder,
                rootCount,
                officeJoinCount,
                userJoinCount
        )));
        tupleQuery.distinct(true);

        return this.selectQuery(builder, query, tupleQuery, root, page, pageSize);
    }

    @Override
    public List<Doctor> findByUser(User user) {
        return this.findBy(Doctor_.user, user);
    }

    @Override
    protected void insertOrderBy(CriteriaBuilder builder, CriteriaQuery<Doctor> query, Root<Doctor> root) {
        query.orderBy(builder.asc(root.get(Doctor_.user).get(User_.firstName)), builder.asc(root.get(Doctor_.user).get(User_.surname)));
    }

    private Predicate[] getPredicates(Collection<String> names,
                                      Collection<String> surnames,
                                      Collection<Office> offices,
                                      Collection<DoctorSpecialty> doctorSpecialties,
                                      Collection<Locality> localities,
                                      CriteriaBuilder builder,
                                      Root<Doctor> root,
                                      Join<Doctor, Office> officeJoin,
                                      Join<Doctor, User> userJoin
    ) {
        List<Predicate> predicates = new LinkedList<>();
        Predicate predicate;
        predicate = this.getNamePredicate(names, surnames, builder, userJoin);
        if (predicate != null) predicates.add(predicate);
        predicate = this.getOfficePredicate(offices, root);
        if (predicate != null) predicates.add(predicate);
        predicate = this.getDoctorSpecialtyPredicate(doctorSpecialties, builder, root);
        if (predicate != null) predicates.add(predicate);
        predicate = this.getLocalityPredicate(localities, officeJoin);
        if (predicate != null) predicates.add(predicate);

        Predicate[] predicatesArrayCount = new Predicate[predicates.size()];
        return predicates.toArray(predicatesArrayCount);
    }

    private Predicate getLocalityPredicate(Collection<Locality> localities, Join<Doctor, Office> officeJoin) {
        if (localities.isEmpty())
            return null;

        Path<?> expression = officeJoin.get(Office_.locality);
        return expression.in(localities);
    }

    private Predicate getDoctorSpecialtyPredicate(Collection<DoctorSpecialty> doctorSpecialties, CriteriaBuilder builder, Root<Doctor> root) {
        if (doctorSpecialties.isEmpty())
            return null;

        List<Predicate> predicates = new LinkedList<>();
        Expression<Collection<DoctorSpecialty>> expression = root.get(Doctor_.doctorSpecialties);

        for (DoctorSpecialty doctorSpecialty : doctorSpecialties) {
            predicates.add(builder.isMember(doctorSpecialty, expression));
        }

        Predicate[] predicatesArray = new Predicate[predicates.size()];
        predicatesArray = predicates.toArray(predicatesArray);
        return builder.or(predicatesArray);
    }

    private Predicate getOfficePredicate(Collection<Office> offices, Root<Doctor> root) {
        if (offices.isEmpty())
            return null;

        Path<?> expression = root.get(Doctor_.office);
        return expression.in(offices);
    }

    private Predicate getNamePredicate(Collection<String> firstNames, Collection<String> surnames, CriteriaBuilder builder, Join<Doctor, User> userJoin) {
        if (firstNames.isEmpty() && surnames.isEmpty())
            return null;

        List<Predicate> predicates = new LinkedList<>();

        Expression<String> expression = userJoin.get(User_.firstName).as(String.class);
        for (String name : firstNames) {
            if (name.isEmpty()) continue;
            name = name.replace("%", "\\%");
            name = name.replace("_", "\\_");
            predicates.add(
                    builder.like(
                            builder.function(UNACCENT_FUNC, String.class, builder.lower(expression)),
                            StringSearchType.CONTAINS_NO_ACC.transform(name.toLowerCase())
                    )
            );
        }

        expression = userJoin.get(User_.surname).as(String.class);
        for (String name : surnames) {
            if (name.isEmpty()) continue;
            name = name.replace("%", "\\%");
            name = name.replace("_", "\\_");
            predicates.add(
                    builder.like(
                            builder.function(UNACCENT_FUNC, String.class, builder.lower(expression)),
                            StringSearchType.CONTAINS_NO_ACC.transform(name.toLowerCase())
                    )
            );
        }

        Predicate[] predicatesArray = new Predicate[predicates.size()];
        predicatesArray = predicates.toArray(predicatesArray);
        return builder.or(predicatesArray);
    }
}