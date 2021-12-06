package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {
    
    @Override
    public List<String> getFirstNamesReverseSorted(List<User> users) {
        return users.stream().sorted(Comparator.comparing(User::getFirstName).reversed()).
            map(User::getFirstName).collect(Collectors.toList());
    }

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        return users.stream().
            sorted(Comparator.comparing(User::getAge).reversed().thenComparing(User::getFirstName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        return users.stream().flatMap(user -> user.getPrivileges().stream()).
                distinct().collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        return users.stream().
            filter(user -> user.getPrivileges().contains(Privilege.UPDATE) && user.getAge() > age).
                findFirst();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        return users.stream().collect(Collectors.groupingBy(user -> user.getPrivileges().size()));
    }

    @Override
    public double getAverageAgeForUsers(final List<User> users) {
        return users.stream().mapToDouble(User::getAge).average().orElse(-1);
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        Map<String, Long> nrOfOccurrences = users.stream().
                collect(Collectors.groupingBy(User::getLastName, Collectors.counting()));
        return nrOfOccurrences.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).
               filter(entry -> entry.getValue() > 1).map(Map.Entry::getKey).findFirst();
    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        return users.stream().filter(Arrays.stream(predicates).reduce(x -> true, Predicate::and)).collect(Collectors.toList());
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        StringBuilder sb = new StringBuilder();
        users.forEach(user -> sb.append(mapFun.apply(user)).append(delimiter));
        return sb.substring(0, sb.lastIndexOf(delimiter));
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        return users.stream().flatMap(user -> user.getPrivileges().stream().
            map(privilege -> new AbstractMap.SimpleEntry<>(privilege, user))).
                collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                        Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toList())));
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        return users.stream().collect(Collectors.groupingBy(User::getLastName, Collectors.counting()));
    }
}
