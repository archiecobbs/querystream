
/*
 * Copyright (C) 2018 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.querystream.jpa;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

/**
 * {@link SearchStream} containing {@link Integer} values.
 */
public interface IntStream extends ExprStream<Integer, Expression<Integer>> {

// Aggregation

    DoubleValue average();

    IntValue max();

    IntValue min();

    IntValue sum();

// Mapping

    LongStream asLongStream();

    DoubleStream asDoubleStream();

// Narrowing overrides (SearchStream)

    @Override
    IntStream distinct();

    @Override
    IntStream orderBy(Ref<?, ? extends Expression<?>> ref, boolean asc);

    @Override
    IntStream orderBy(Function<? super Expression<Integer>, ? extends Expression<?>> orderExprFunction, boolean asc);

    @Override
    IntStream orderByMulti(Function<? super Expression<Integer>, ? extends List<? extends Order>> orderListFunction);

    @Override
    IntStream groupBy(Ref<?, ? extends Expression<?>> ref);

    @Override
    IntStream groupBy(Function<? super Expression<Integer>, ? extends Expression<?>> groupFunction);

    @Override
    IntStream groupByMulti(Function<? super Expression<Integer>, ? extends List<Expression<?>>> groupFunction);

    @Override
    IntStream having(Function<? super Expression<Integer>, ? extends Expression<Boolean>> havingFunction);

    @Override
    IntValue findAny();

    @Override
    IntValue findFirst();

// Narrowing overrides (QueryStream)

    @Override
    IntStream bind(Ref<Integer, ? super Expression<Integer>> ref);

    @Override
    IntStream peek(Consumer<? super Expression<Integer>> peeker);

    @Override
    <X2, S2 extends Selection<X2>> IntStream bind(
      Ref<X2, ? super S2> ref, Function<? super Expression<Integer>, ? extends S2> refFunction);

    @Override
    <R> IntStream addRoot(Ref<R, ? super Root<R>> ref, Class<R> type);

    @Override
    IntStream filter(Function<? super Expression<Integer>, ? extends Expression<Boolean>> predicateBuilder);

    @Override
    IntStream limit(int maxSize);

    @Override
    IntStream skip(int num);
}
