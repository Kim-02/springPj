package com.dasolsystem.core.enums;

public enum Role {
    User(1),
    Member(2),
    Manager(3),
    Presidency(4);

    private final int level;
    Role(int level) {this.level = level;}
    public int getLevel() {return level;}

    /** this 가 other 보다 상위(=동일 레벨 포함)인지 */
    public boolean isAtLeast(Role other) {
        return this.level >= other.level;
    }
    /** this 가 other 보다 하위(=동일 레벨 포함)인지 */
    public boolean isAtMost(Role other) {
        return this.level <= other.level;
    }
}
