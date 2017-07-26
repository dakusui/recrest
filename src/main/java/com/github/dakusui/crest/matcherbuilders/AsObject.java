package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.InternalUtils;
import com.sun.javafx.binding.SelectBinding;
import org.hamcrest.Matcher;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class AsObject<I, S extends AsObject<I, S>> implements Crest<I, Object, S> {
  private final Function<? super I, ?>          function;
  private final List<Predicate<? super Object>> predicates;

  @SuppressWarnings("WeakerAccess")
  public AsObject(Function<? super I, ?> function) {
    this.function = Objects.requireNonNull(function);
    this.predicates = new LinkedList<>();
  }

  @Override
  @SuppressWarnings("unchecked")
  public S check(Predicate<? super Object> predicate) {
    this.predicates.add(predicate);
    return (S) this;
  }

  @Override
  public Matcher<? super I> all() {
    return matcher(Op.AND);
  }

  @Override
  public Matcher<? super I> any() {
    return matcher(Op.OR);
  }

  @SuppressWarnings("unchecked")
  private Matcher<? super I> matcher(Op op) {
    InternalUtils.requireState(!predicates.isEmpty());
    return (predicates.size() == 1) ?
        InternalUtils.toMatcher(predicates.get(0), this.function) :
        Objects.requireNonNull(op).create(predicates, this.function);
  }
}
