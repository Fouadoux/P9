package com.glucovion.authservice.model;

/**
 * Enum representing roles available in the application.
 *
 * <ul>
 *   <li><b>ADMIN</b>: Full access to all features.</li>
 *   <li><b>USER</b>: Standard user with limited permissions.</li>
 *   <li><b>PENDING</b>: Newly registered user awaiting activation.</li>
 * </ul>
 */
public enum AppRole {
    ADMIN, USER, PENDING
}
