package com.github.dakusui.crest.utils.printable;

import com.github.dakusui.crest.core.InternalUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public enum Predicates {
  ;

  public static <T> Predicate<T> alwaysTrue() {
    return Printable.predicate("alwaysTrue", t -> true);
  }

  public static Predicate<? super Boolean> isTrue() {
    return Printable.predicate("isTrue", (Boolean v) -> v);
  }

  public static Predicate<Boolean> isFalse() {
    return Printable.predicate("isFalse", (Boolean v) -> !v);
  }

  public static <T> Predicate<T> isNull() {
    return Printable.predicate("isNull", Objects::isNull);
  }

  public static <T> Predicate<T> isNotNull() {
    return Printable.predicate("isNotNull", Objects::nonNull);
  }

  public static <T> Predicate<T> equalTo(T value) {
    return Printable.predicate(
        () -> String.format("equalTo[%s]", value),
        v -> Objects.equals(v, value)
    );
  }

  public static <T> Predicate<? super T> isSameAs(T value) {
    return Printable.predicate(
        () -> String.format("==[%s]", value),
        v -> v == value
    );
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<? super T> isInstanceOf(Class<?> value) {
    requireNonNull(value);
    //noinspection SimplifiableConditionalExpression
    return Printable.predicate(
        () -> String.format("isInstanceOf[%s]", value.getCanonicalName()),
        v -> v == null ?
            false :
            value.isAssignableFrom(v.getClass())
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> gt(T value) {
    return Printable.predicate(
        () -> String.format(">[%s]", value),
        v -> v.compareTo(value) > 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> ge(T value) {
    return Printable.predicate(
        () -> String.format(">=[%s]", value),
        v -> v.compareTo(value) >= 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> lt(T value) {
    return Printable.predicate(
        () -> String.format("<[%s]", value),
        v -> v.compareTo(value) < 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> le(T value) {
    return Printable.predicate(
        () -> String.format("<=[%s]", value),
        v -> v.compareTo(value) <= 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> eq(T value) {
    return Printable.predicate(
        () -> String.format("=[%s]", value),
        v -> v.compareTo(value) == 0
    );
  }

  public static Predicate<? super String> matchesRegex(String regex) {
    requireNonNull(regex);
    return Printable.predicate(
        () -> String.format("matchesRegex[%s]", regex),
        s -> s.matches(regex)
    );
  }

  public static Predicate<? super String> containsString(String string) {
    requireNonNull(string);
    return Printable.predicate(
        () -> String.format("containsString[%s]", string),
        s -> s.contains(string)
    );
  }

  public static Predicate<? super String> startsWith(String string) {
    requireNonNull(string);
    return Printable.predicate(
        () -> String.format("startsWith[%s]", string),
        s -> s.startsWith(string)
    );
  }

  public static Predicate<? super String> endsWith(String string) {
    requireNonNull(string);
    return Printable.predicate(
        () -> String.format("endsWith[%s]", string),
        s -> s.endsWith(string)
    );
  }

  public static Predicate<? super String> equalsIgnoreCase(String string) {
    requireNonNull(string);
    return Printable.predicate(
        () -> String.format("equalsIgnoreCase[%s]", string),
        s -> s.equalsIgnoreCase(string)
    );
  }

  public static Predicate<? super String> isEmptyString() {
    return Printable.predicate(
        "isEmpty",
        String::isEmpty
    );
  }

  public static Predicate<? super String> isEmptyOrNullString() {
    return Printable.predicate(
        "isEmptyOrNullString",
        s -> Objects.isNull(s) || isEmptyString().test(s)
    );
  }

  public static <E> Predicate<? super Collection<E>> contains(Object entry) {
    requireNonNull(entry);
    //noinspection SuspiciousMethodCalls
    return Printable.predicate(
        () -> String.format("contains[%s]", entry),
        c -> c.contains(entry)
    );
  }

  public static Predicate<? super Collection> isEmpty() {
    return Printable.predicate("isEmpty", Collection::isEmpty);
  }

  public static <E> Predicate<? super Stream<? extends E>> allMatch(Predicate<E> predicate) {
    return Printable.predicate(
        () -> String.format("allMatch[%s]", requireNonNull(predicate)),
        stream -> stream.allMatch(predicate)
    );
  }

  public static <E> Predicate<? super Stream<? extends E>> noneMatch(Predicate<E> predicate) {
    return Printable.predicate(
        () -> String.format("noneMatch[%s]", requireNonNull(predicate)),
        stream -> stream.noneMatch(predicate)
    );
  }

  public static <E> Predicate<? super Stream<? extends E>> anyMatch(Predicate<E> predicate) {
    return Printable.predicate(
        () -> String.format("anyMatch[%s]", requireNonNull(predicate)),
        stream -> stream.anyMatch(predicate)
    );
  }

  public static <T> Predicate<? super T> invoke(String methodName, Object... args) {
    return Printable.predicate(
        () -> String.format("@%s%s", methodName, asList(args)),
        (Object target) -> (boolean) InternalUtils.invokeMethod(target, methodName, args)
    );
  }

}