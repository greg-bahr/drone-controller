/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE LOCAL_POSITION_NED PACKING
package dev.gregbahr.groundcontrol.mavlink.common;
import dev.gregbahr.groundcontrol.mavlink.MAVLinkPacket;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkMessage;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkPayload;
        
/**
 * The filtered local position (e.g. fused computer vision and accelerometers). Coordinate frame is right-handed, Z-axis down (aeronautical frame, NED / north-east-down convention)
 */
public class msg_local_position_ned extends MAVLinkMessage {

    public static final int MAVLINK_MSG_ID_LOCAL_POSITION_NED = 32;
    public static final int MAVLINK_MSG_LENGTH = 28;
    private static final long serialVersionUID = MAVLINK_MSG_ID_LOCAL_POSITION_NED;

      
    /**
     * Timestamp (time since system boot).
     */
    public long time_boot_ms;
      
    /**
     * X Position
     */
    public float x;
      
    /**
     * Y Position
     */
    public float y;
      
    /**
     * Z Position
     */
    public float z;
      
    /**
     * X Speed
     */
    public float vx;
      
    /**
     * Y Speed
     */
    public float vy;
      
    /**
     * Z Speed
     */
    public float vz;
    

    /**
     * Generates the payload for a mavlink message for a message of this type
     * @return
     */
    @Override
    public MAVLinkPacket pack() {
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH,isMavlink2);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_LOCAL_POSITION_NED;
        
        packet.payload.putUnsignedInt(time_boot_ms);
        packet.payload.putFloat(x);
        packet.payload.putFloat(y);
        packet.payload.putFloat(z);
        packet.payload.putFloat(vx);
        packet.payload.putFloat(vy);
        packet.payload.putFloat(vz);
        
        if (isMavlink2) {
            
        }
        return packet;
    }

    /**
     * Decode a local_position_ned message into this class fields
     *
     * @param payload The message to decode
     */
    @Override
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
        
        this.time_boot_ms = payload.getUnsignedInt();
        this.x = payload.getFloat();
        this.y = payload.getFloat();
        this.z = payload.getFloat();
        this.vx = payload.getFloat();
        this.vy = payload.getFloat();
        this.vz = payload.getFloat();
        
        if (isMavlink2) {
            
        }
    }

    /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_local_position_ned() {
        this.msgid = MAVLINK_MSG_ID_LOCAL_POSITION_NED;
    }
    
    /**
     * Constructor for a new message, initializes msgid and all payload variables
     */
    public msg_local_position_ned( long time_boot_ms, float x, float y, float z, float vx, float vy, float vz) {
        this.msgid = MAVLINK_MSG_ID_LOCAL_POSITION_NED;

        this.time_boot_ms = time_boot_ms;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        
    }
    
    /**
     * Constructor for a new message, initializes everything
     */
    public msg_local_position_ned( long time_boot_ms, float x, float y, float z, float vx, float vy, float vz, int sysid, int compid, boolean isMavlink2) {
        this.msgid = MAVLINK_MSG_ID_LOCAL_POSITION_NED;
        this.sysid = sysid;
        this.compid = compid;
        this.isMavlink2 = isMavlink2;

        this.time_boot_ms = time_boot_ms;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     *
     */
    public msg_local_position_ned(MAVLinkPacket mavLinkPacket) {
        this.msgid = MAVLINK_MSG_ID_LOCAL_POSITION_NED;
        
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.isMavlink2 = mavLinkPacket.isMavlink2;
        unpack(mavLinkPacket.payload);
    }

                  
    /**
     * Returns a string with the MSG name and data
     */
    @Override
    public String toString() {
        return "MAVLINK_MSG_ID_LOCAL_POSITION_NED - sysid:"+sysid+" compid:"+compid+" time_boot_ms:"+time_boot_ms+" x:"+x+" y:"+y+" z:"+z+" vx:"+vx+" vy:"+vy+" vz:"+vz+"";
    }
    
    /**
     * Returns a human-readable string of the name of the message
     */
    @Override
    public String name() {
        return "MAVLINK_MSG_ID_LOCAL_POSITION_NED";
    }
}
        