/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

package dev.gregbahr.groundcontrol.mavlink.enums;

/** 
 * Sequence that motors are tested when using MAV_CMD_DO_MOTOR_TEST.
 */
public class MOTOR_TEST_ORDER {
   public static final int MOTOR_TEST_ORDER_DEFAULT = 0; /* Default autopilot motor test method. | */
   public static final int MOTOR_TEST_ORDER_SEQUENCE = 1; /* Motor numbers are specified as their index in a predefined vehicle-specific sequence. | */
   public static final int MOTOR_TEST_ORDER_BOARD = 2; /* Motor numbers are specified as the output as labeled on the board. | */
   public static final int MOTOR_TEST_ORDER_ENUM_END = 3; /*  | */
}
            