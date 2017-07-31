package com.github.dakusui.crest.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public interface Matcher<T> {
  boolean matches(T value, Assertion<? extends T> session);

  List<String> describeExpectation(Assertion<? extends T> session);

  List<String> describeMismatch(T value, Assertion<? extends T> session);

  interface Composite<T> extends Matcher<T> {
    abstract class Base<T> implements Composite<T> {
      private final List<Matcher<T>> children;

      @SuppressWarnings("unchecked")
      Base(List<Matcher<? super T>> children) {
        this.children = (List<Matcher<T>>) Collections.<T>unmodifiableList((List<? extends T>) requireNonNull(children));
      }

      @Override
      public boolean matches(T value, Assertion<? extends T> session) {
        boolean ret = first();
        for (Matcher<T> eachChild : children())
          ret = op(ret, eachChild.matches(value, session));
        return ret;
      }

      @Override
      public List<String> describeExpectation(Assertion<? extends T> session) {
        return new LinkedList<String>() {{
          add(String.format("%s:[", name()));
          children().forEach(
              (Matcher<T> eachChild) -> {
                List<String> formattedExpectation = eachChild.describeExpectation(session);
                if (formattedExpectation.isEmpty())
                  return;
                if (formattedExpectation.size() == 1)
                  add(String.format("  %s", eachChild.describeExpectation(session).get(0)));
                else {
                  add(
                      String.format("  %s", eachChild.describeExpectation(session))
                  );
                }
              }
          );
          add("]");
        }};
      }

      @Override
      public List<String> describeMismatch(T value, Assertion<? extends T> session) {
        return new LinkedList<String>() {{
          add(String.format("when x=%s; then %s:[", InternalUtils.formatValue(value), name()));
          for (Matcher<T> eachChild : children()) {
            if (eachChild.matches(value, session))
              addAll(indent(eachChild.describeExpectation(session)));
            else
              addAll(indent(eachChild.describeMismatch(value, session)));
          }
          add(String.format("]->%s", matches(value, session)));
        }};
      }

      List<String> indent(List<String> in) {
        return in.stream().map(s -> "  " + s).collect(toList());
      }

      List<Matcher<T>> children() {
        return this.children;
      }

      abstract protected String name();

      abstract protected boolean first();

      abstract protected boolean op(boolean current, boolean next);
    }
  }

  interface Conjunctive<T> extends Composite<T> {
    @SuppressWarnings("unchecked")
    static <T> Matcher<T> create(List<Matcher<? super T>> matchers) {
      return new Conjunctive.Base<T>(matchers) {
        @Override
        protected String name() {
          return "and";
        }

        @Override
        protected boolean first() {
          return true;
        }

        @Override
        protected boolean op(boolean current, boolean next) {
          return current && next;
        }
      };
    }
  }

  interface Disjunctive<T> extends Composite<T> {
    @SuppressWarnings("unchecked")
    static <T> Matcher<T> create(List<Matcher<? super T>> matchers) {
      return new Composite.Base<T>(matchers) {

        @Override
        protected String name() {
          return "or";
        }

        @Override
        protected boolean first() {
          return false;
        }

        @Override
        protected boolean op(boolean current, boolean next) {
          return current || next;
        }
      };
    }
  }

  interface Leaf<T> extends Matcher<T> {
    static <I, O> Matcher<I> create(Predicate<? super O> p, Function<? super I, ? extends O> function) {
      return new Matcher<I>() {
        @Override
        public boolean matches(I value, Assertion<? extends I> session) {
          return session.test(p, session.apply(function, value));
        }

        @Override
        public List<String> describeExpectation(Assertion<? extends I> session) {
          return singletonList(InternalUtils.formatExpectation(p, function));
        }

        @Override
        public List<String> describeMismatch(I value, Assertion<? extends I> session) {
          return singletonList(String.format(
              "%s was false because %s=%s does not satisfy it",
              InternalUtils.formatExpectation(p, function),
              InternalUtils.formatFunction(function, "x"),
              InternalUtils.formatValue(session.apply(function, value))
              )
          );
        }
      };
    }
  }
}