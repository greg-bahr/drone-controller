/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE OPEN_DRONE_ID_SYSTEM PACKING
package dev.gregbahr.groundcontrol.mavlink.common;
import dev.gregbahr.groundcontrol.mavlink.MAVLinkPacket;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkMessage;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkPayload;
        
/**
 * Data for filling the OpenDroneID System message. The System Message contains general system information including the operator location and possible aircraft group information.
 */
public class msg_open_drone_id_system extends MAVLinkMessage {

    public static final int MAVLINK_MSG_ID_OPEN_DRONE_ID_SYSTEM = 12904;
    public static final int MAVLINK_MSG_LENGTH = 50;
    private static final long serialVersionUID = MAVLINK_MSG_ID_OPEN_DRONE_ID_SYSTEM;

      
    /**
     * Latitude of the operator. If unknown: 0 (both Lat/Lon).
     */
    public int operator_latitude;
      
    /**
     * Longitude of the operator. If unknown: 0 (both Lat/Lon).
     */
    public int operator_longitude;
      
    /**
     * Area Operations Ceiling relative to WGS84. If unknown: -1000 m.
     */
    public float area_ceiling;
      
    /**
     * Area Operations Floor relative to WGS84. If unknown: -1000 m.
     */
    public float area_floor;
      
    /**
     * Geodetic altitude of the operator relative to WGS84. If unknown: -1000 m.
     */
    public float operator_altitude_geo;
      
    /**
     * Number of aircraft in the area, group or formation (default 1).
     */
    public int area_count;
      
    /**
     * Radius of the cylindrical area of the group or formation (default 0).
     */
    public int area_radius;
      
    /**
     * System ID (0 for broadcast).
     */
    public short target_system;
      
    /**
     * Component ID (0 for broadcast).
     */
    public short target_component;
      
    /**
     * Only used for drone ID data received from other UAs. See detailed description at https://mavlink.io/en/services/opendroneid.html. 
     */
    public short id_or_mac[] = new short[20];
      
    /**
     * Specifies the operator location type.
     */
    public short operator_location_type;
      
    /**
     * Specifies the classification type of the UA.
     */
    public short classification_type;
      
    /**
     * When classification_type is MAV_ODID_CLASSIFICATION_TYPE_EU, specifies the category of the UA.
     */
    public short category_eu;
      
    /**
     * When classification_type is MAV_ODID_CLASSIFICATION_TYPE_EU, specifies the class of the UA.
     */
    public short class_eu;
    

    /**
     * Generates the payload for a mavlink message for a message of this type
     * @return
     */
    @Override
    public MAVLinkPacket pack() {
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH,isMavlink2);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_OPEN_DRONE_ID_SYSTEM;
        
        packet.payload.putInt(operator_latitude);
        packet.payload.putInt(operator_longitude);
        packet.payload.putFloat(area_ceiling);
        packet.payload.putFloat(area_floor);
        packet.payload.putFloat(operator_altitude_geo);
        packet.payload.putUnsignedShort(area_count);
        packet.payload.putUnsignedShort(area_radius);
        packet.payload.putUnsignedByte(target_system);
        packet.payload.putUnsignedByte(target_component);
        
        for (int i = 0; i < id_or_mac.length; i++) {
            packet.payload.putUnsignedByte(id_or_mac[i]);
        }
                    
        packet.payload.putUnsignedByte(operator_location_type);
        packet.payload.putUnsignedByte(classification_type);
        packet.payload.putUnsignedByte(category_eu);
        packet.payload.putUnsignedByte(class_eu);
        
        if (isMavlink2) {
            
        }
        return packet;
    }

    /**
     * Decode a open_drone_id_system message into this class fields
     *
     * @param payload The message to decode
     */
    @Override
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
        
        this.operator_latitude = payload.getInt();
        this.operator_longitude = payload.getInt();
        this.area_ceiling = payload.getFloat();
        this.area_floor = payload.getFloat();
        this.operator_altitude_geo = payload.getFloat();
        this.area_count = payload.getUnsignedShort();
        this.area_radius = payload.getUnsignedShort();
        this.target_system = payload.getUnsignedByte();
        this.target_component = payload.getUnsignedByte();
         
        for (int i = 0; i < this.id_or_mac.length; i++) {
            this.id_or_mac[i] = payload.getUnsignedByte();
        }
                
        this.operator_location_type = payload.getUnsignedByte();
        this.classification_type = payload.getUnsignedByte();
        this.category_eu = payload.getUnsignedByte();
        this.class_eu = payload.getUnsignedByte();
        
        if (isMavlink2) {
            
        }
    }

    /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_open_drone_id_system() {
        this.msgid = MAVLINK_MSG_ID_OPEN_DRONE_ID_SYSTEM;
    }
    
    /**
     * Constructor for a new message, initializes msgid and all payload variables
     */
    public msg_open_drone_id_system( int operator_latitude, int operator_longitude, float area_ceiling, float area_floor, float operator_altitude_geo, int area_count, int area_radius, short target_system, short target_component, short[] id_or_mac, short operator_location_type, short classification_type, short category_eu, short class_eu) {
        this.msgid = MAVLINK_MSG_ID_OPEN_DRONE_ID_SYSTEM;

        this.operator_latitude = operator_latitude;
        this.operator_longitude = operator_longitude;
        this.area_ceiling = area_ceiling;
        this.area_floor = area_floor;
        this.operator_altitude_geo = operator_altitude_geo;
        this.area_count = area_count;
        this.area_radius = area_radius;
        this.target_system = target_system;
        this.target_component = target_component;
        this.id_or_mac = id_or_mac;
        this.operator_location_type = operator_location_type;
        this.classification_type = classification_type;
        this.category_eu = category_eu;
        this.class_eu = class_eu;
        
    }
    
    /**
     * Constructor for a new message, initializes everything
     */
    public msg_open_drone_id_system( int operator_latitude, int operator_longitude, float area_ceiling, float area_floor, float operator_altitude_geo, int area_count, int area_radius, short target_system, short target_component, short[] id_or_mac, short operator_location_type, short classification_type, short category_eu, short class_eu, int sysid, int compid, boolean isMavlink2) {
        this.msgid = MAVLINK_MSG_ID_OPEN_DRONE_ID_SYSTEM;
        this.sysid = sysid;
        this.compid = compid;
        this.isMavlink2 = isMavlink2;

        this.operator_latitude = operator_latitude;
        this.operator_longitude = operator_longitude;
        this.area_ceiling = area_ceiling;
        this.area_floor = area_floor;
        this.operator_altitude_geo = operator_altitude_geo;
        this.area_count = area_count;
        this.area_radius = area_radius;
        this.target_system = target_system;
        this.target_component = target_component;
        this.id_or_mac = id_or_mac;
        this.operator_location_type = operator_location_type;
        this.classification_type = classification_type;
        this.category_eu = category_eu;
        this.class_eu = class_eu;
        
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     *
     */
    public msg_open_drone_id_system(MAVLinkPacket mavLinkPacket) {
        this.msgid = MAVLINK_MSG_ID_OPEN_DRONE_ID_SYSTEM;
        
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
        return "MAVLINK_MSG_ID_OPEN_DRONE_ID_SYSTEM - sysid:"+sysid+" compid:"+compid+" operator_latitude:"+operator_latitude+" operator_longitude:"+operator_longitude+" area_ceiling:"+area_ceiling+" area_floor:"+area_floor+" operator_altitude_geo:"+operator_altitude_geo+" area_count:"+area_count+" area_radius:"+area_radius+" target_system:"+target_system+" target_component:"+target_component+" id_or_mac:"+id_or_mac+" operator_location_type:"+operator_location_type+" classification_type:"+classification_type+" category_eu:"+category_eu+" class_eu:"+class_eu+"";
    }
    
    /**
     * Returns a human-readable string of the name of the message
     */
    @Override
    public String name() {
        return "MAVLINK_MSG_ID_OPEN_DRONE_ID_SYSTEM";
    }
}
        