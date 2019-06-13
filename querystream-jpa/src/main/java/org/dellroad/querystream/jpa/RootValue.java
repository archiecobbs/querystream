
/*
 * Copyright (C) 2018 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.querystream.jpa;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.SingularAttribute;

/**
 * A {@link RootStream} that is guaranteed to return at most a single result.
 */
public interface RootValue<X> extends FromValue<X, Root<X>>, RootStream<X> {

// Narrowing overrides (QueryStream)

    @Override
    RootValue<X> bind(Ref<X, ? super Root<X>> ref);

    @Override
    RootValue<X> peek(Consumer<? super Root<X>> peeker);

    @Override
    <X2, S2 extends Selection<X2>> RootValue<X> bind(Ref<X2, ? super S2> ref, Function<? super Root<X>, ? extends S2> refFunction);

    @Override
    RootValue<X> filter(SingularAttribute<? super X, Boolean> attribute);

    @Override
    RootValue<X> filter(Function<? super Root<X>, ? extends Expression<Boolean>> predicateBuilder);

    @Override
    RootValue<X> withFlushMode(FlushModeType flushMode);

    @Override
    RootValue<X> withLockMode(LockModeType lockMode);

    @Override
    RootValue<X> withHint(String name, Object value);

    @Override
    RootValue<X> withHints(Map<String, Object> hints);

    @Override
    <T> RootValue<X> withParam(Parameter<T> parameter, T value);

    @Override
    RootValue<X> withParam(Parameter<Date> parameter, Date value, TemporalType temporalType);

    @Override
    RootValue<X> withParam(Parameter<Calendar> parameter, Calendar value, TemporalType temporalType);

    @Override
    RootValue<X> withParams(Set<ParamBinding<?>> params);

    @Override
    RootValue<X> withLoadGraph(String name);

    @Override
    RootValue<X> withFetchGraph(String name);
}
