package com.github.ruediste.rise.crud;

public interface CrudUseCases {

    /**
     * Browsing and searching through all instances of a certain type. The
     * instances can be viewed, edited and deleted. New instances can be
     * created. Certain actions can be triggered directly per instance.
     */
    @UseCase(stakeHolder = StakeHolder.USER)
    interface BrowseTypes {
    }

    enum StakeHolder {
        USER
    }

    @interface UseCase {
        StakeHolder stakeHolder();

    }

    @interface Covers {
        Class<?> value();
    }

    @interface Implements {
        Class<?> value();
    }

    @Covers(BrowseTypes.class)
    @Implements(BrowseTypes.class)
    interface TestImp {

    }
}
