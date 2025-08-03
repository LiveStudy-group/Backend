package org.livestudy.domain.title;



public interface TitleCondition {

    boolean isSatisfied(Long  userId);
    TitleCode getTitleCode();
}
