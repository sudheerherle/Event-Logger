/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventlogger;

/**
 *
 * @author I14746
 */
public enum EventDescription {
    EVENT_SM_POWERED_ON(0),
    EVENT_DATE_TIME_CHANGED(1),
    EVENT_DIRECT_OUT_COUNT(2),
    EVENT_US_BLOCK_CLEAR(3),
    EVENT_US_BLOCK_OCCUPIED(4),
    EVENT_DS_BLOCK_CLEAR(5),
    EVENT_DS_BLOCK_OCCUPIED(6),
    EVENT_DAC_US_RESET_APPLIED(7),
    EVENT_DAC_US_RESET_COMPLETED(8),
    EVENT_SYSTEM_NORMAL(9),
    EVENT_SYSTEM_DEFECTIVE(10),
    EVENT_BOARDS_MISSING(11),
    EVENT_ALL_BOARDS_FOUND(12),
    EVENT_AD1_BOARD_MISSING(13),
    EVENT_AD1_BOARD_FOUND(14),
    EVENT_AD2_BOARD_MISSING(15),
    EVENT_AD2_BOARD_FOUND(16),
    EVENT_MODEM_BOARD_A_MISSING(17),
    EVENT_MODEM_BOARD_A_FOUND(18),
    EVENT_MODEM_BOARD_B_MISSING(19),
    EVENT_MODEM_BOARD_B_FOUND(20),
    EVENT_RELAY_BOARD_A_MISSING(21),
    EVENT_RELAY_BOARD_A_FOUND(22),
    EVENT_RELAY_BOARD_B_MISSING(23),
    EVENT_RELAY_BOARD_B_FOUND(24),
    EVENT_PEER_CPU_MISSING(25),
    EVENT_PEER_CPU_FOUND(26),
    EVENT_LU1_US1_COMM_FAILURE(27),
    EVENT_LU1_US1_COMM_RESTORED(28),
    EVENT_LU1_US2_COMM_FAILURE(29),
    EVENT_LU1_US2_COMM_RESTORED(30),
    EVENT_LU1_DS1_COMM_FAILURE(31),
    EVENT_LU1_DS1_COMM_RESTORED(32),
    EVENT_LU1_DS2_COMM_FAILURE(33),
    EVENT_LU1_DS2_COMM_RESTORED(34),
    EVENT_LU2_US1_COMM_FAILURE(35),
    EVENT_LU2_US1_COMM_RESTORED(36),
    EVENT_LU2_US2_COMM_FAILURE(37),
    EVENT_LU2_US2_COMM_RESTORED(38),
    EVENT_LU2_DS1_COMM_FAILURE(39),
    EVENT_LU2_DS1_COMM_RESTORED(40),
    EVENT_LU2_DS2_COMM_FAILURE(41),
    EVENT_LU2_DS2_COMM_RESTORED(42),
    EVENT_US1_LU1_COMM_FAILURE(43),
    EVENT_US1_LU1_COMM_RESTORED(44),
    EVENT_US2_LU1_COMM_FAILURE(45),
    EVENT_US2_LU1_COMM_RESTORED(46),
    EVENT_DS1_LU1_COMM_FAILURE(47),
    EVENT_DS1_LU1_COMM_RESTORED(48),
    EVENT_DS2_LU1_COMM_FAILURE(49),
    EVENT_DS2_LU1_COMM_RESTORED(50),
    EVENT_US1_LU2_COMM_FAILURE(51),
    EVENT_US1_LU2_COMM_RESTORED(52),
    EVENT_US2_LU2_COMM_FAILURE(53),
    EVENT_US2_LU2_COMM_RESTORED(54),
    EVENT_DS1_LU2_COMM_FAILURE(55),
    EVENT_DS1_LU2_COMM_RESTORED(56),
    EVENT_DS2_LU2_COMM_FAILURE(57),
    EVENT_DS2_LU2_COMM_RESTORED(58),
    EVENT_PEER_CPU_COMM_FAILURE(59),
    EVENT_PEER_CPU_COMM_RESTORED(60),
    EVENT_AD1_FAILED(61),
    EVENT_AD1_NORMAL(62),
    EVENT_AD2_FAILED(63),
    EVENT_AD2_NORMAL(64),
    EVENT_PEER_CPU_FAILED(65),
    EVENT_PEER_CPU_NORMAL(66),
    EVENT_VITAL_RELAY_A_FAILED(67),
    EVENT_VITAL_RELAY_A_NORMAL(68),
    EVENT_VITAL_RELAY_B_FAILED(69),
    EVENT_VITAL_RELAY_B_NORMAL(70),
    EVENT_PREP_RELAY_A_FAILED(71),
    EVENT_PREP_RELAY_A_NORMAL(72),
    EVENT_MODEM_A_ERROR(73),
    EVENT_MODEM_A_NORMAL(74),
    EVENT_MODEM_B_ERROR(75),
    EVENT_MODEM_B_NORMAL(76),
    EVENT_US_DAC_FAILED(77),
    EVENT_US_DAC_NORMAL(78),
    EVENT_DS_DAC_FAILED(79),
    EVENT_DS_DAC_NORMAL(80),
    EVENT_US1_POWER_FAILED(81),
    EVENT_US1_POWER_RESTORED(82),
    EVENT_US2_POWER_FAILED(83),
    EVENT_US2_POWER_RESTORED(84),
    EVENT_DS1_POWER_FAILED(85),
    EVENT_DS1_POWER_RESTORED(86),
    EVENT_DS2_POWER_FAILED(87),
    EVENT_DS2_POWER_RESTORED(88),
    EVENT_VDD_VOLTAGE_HIGH(89),
    EVENT_VDD_VOLTAGE_LOW(90),
    EVENT_VDD_VOLTAGE_NORMAL(91),
    EVENT_DAC_DS_RESET_APPLIED(92),
    EVENT_DAC_DS_RESET_COMPLETED(93),
    EVENT_PREP_RELAY_B_FAILED(94),
    EVENT_PREP_RELAY_B_NORMAL(95),
    EVENT_AD_PULSE_MISMATCH(96),
    EVENT_BOOTUP_AD_FAILED(97),
    EVENT_AD1_AND_AD2_PULSATING(98),
    EVENT_AD1_SUP_LOW(99),
    EVENT_AD2_SUP_LOW(100),
    EVENT_AD1_PULSATING(101),
    EVENT_AD2_PULSATING(102),
    EVENT_AD_STATE_MISSING(103),
    EVENT_AD_SUP_PULSATING(104),
    EVENT_AD_STATE_FAIL(105),
    EVENT_AD_NOT_DETECTING(106),
    EVENT_AD_SUP_MISSING(107),
    EVENT_YLED_BAD(108),
    EVENT_RLED_BAD(109),
    EVENT_SPEAKER_BAD(110),
    EVENT_THEFT(111),
    EVENT_SYSTEM_DOOR_OPEN(112),
    EVENT_YLED_RESTORED(113),
    EVENT_RLED_RESTORED(114),
    EVENT_SPEAKER_RESTORED(115),
    EVENT_THEFT_RESTORED(116),
    EVENT_SYSTEM_DOOR_CLOSED(117),
    EVENT_MEMORY_ERASED(118),
    EVENT_AXLE_DECEPTIVE(119),
    EVENT_US_BLOCK_CLEAR_FWD_CNT(120),
    EVENT_US_BLOCK_OCCUPIED_FWD_CNT(121),
    EVENT_DS_BLOCK_CLEAR_FWD_CNT(122),
    EVENT_DS_BLOCK_OCCUPIED_FWD_CNT(123),
    EVENT_US_BLOCK_CLEAR_REV_CNT(124),
    EVENT_US_BLOCK_OCCUPIED_REV_CNT(125),
    EVENT_DS_BLOCK_CLEAR_REV_CNT(126),
    EVENT_DS_BLOCK_OCCUPIED_REV_CNT(127),
    EVENT_US_BLOCK_CLEAR_ENTRY_CNT(128),
    EVENT_US_BLOCK_OCCUPIED_ENTRY_CNT(129),
    EVENT_DS_BLOCK_CLEAR_ENTRY_CNT(130),
    EVENT_DS_BLOCK_OCCUPIED_ENTRY_CNT(131),
    EVENT_US_BLOCK_CLEAR_EXIT_CNT(132),
    EVENT_US_BLOCK_OCCUPIED_EXIT_CNT(133),
    EVENT_DS_BLOCK_CLEAR_EXIT_CNT(134),
    EVENT_DS_BLOCK_OCCUPIED_EXIT_CNT(135),
    EVENT_BATTERY_VOLTAGE_HIGH(136),
    EVENT_BATTERY_VOLTAGE_LOW(137),
    EVENT_BATTERY_VOLTAGE_NORMAL(138),
    EVENT_RESET_BOX_POWERED_ON(139),
    EVENT_NOT_OCCURRED(255);
    private int val;

    private EventDescription(int val) {
        this.val = val;
    }

    public int getOrdinal() {
        return val;
    }
    
    public static int getAdjustedEventID(int eID) {
        int adjustedEventID = eID;
        switch (eID) {
            case 2:
                adjustedEventID = 13;
                break;
            case 71:
                adjustedEventID = 0;
                break;
            case 94:
                adjustedEventID = 1;
                break;
            case 67:
                adjustedEventID = 2;
                break;
            case 69:
                adjustedEventID = 3;
                break;
            case 85:
                adjustedEventID = 4;
                break;
            case 87:
                adjustedEventID = 5;
                break;
            case 81:
                adjustedEventID = 6;
                break;
            case 83:
                adjustedEventID = 7;
                break;
            case 107:
                adjustedEventID = 14;
                break;
            case 99:
                adjustedEventID = 16;
                break;
            case 100:
                adjustedEventID = 17;
                break;
            case 101:
                adjustedEventID = 18;
                break;
            case 102:
                adjustedEventID = 19;
                break;
            case 103:
                adjustedEventID = 20;
                break;
            case 104:
                adjustedEventID = 21;
                break;
            case 105:
                adjustedEventID = 22;
                break;
            case 106:
                adjustedEventID = 23;
                break;
            case 13:
                adjustedEventID = 24;
                break;
            case 15:
                adjustedEventID = 25;
                break;
            case 17:
                adjustedEventID = 27;
                break;
            case 19:
                adjustedEventID = 28;
                break;
            case 21:
                adjustedEventID = 29;
                break;
            case 23:
                adjustedEventID = 30;
                break;
            case 25:
                adjustedEventID = 31;
                break;
            case 79:
                adjustedEventID = 34;
                break;
            case 77:
                adjustedEventID = 35;
                break;
            case 96:
                adjustedEventID = 36;
                break;
            case 97:
                adjustedEventID = 37;
                break;
            case 119:
                adjustedEventID = 38;
                break;
            case 27:
                adjustedEventID = 40;
                break;
            case 29:
                adjustedEventID = 41;
                break;
            case 31:
                adjustedEventID = 42;
                break;
            case 33:
                adjustedEventID = 43;
                break;
            case 43:
                adjustedEventID = 44;
                break;
            case 45:
                adjustedEventID = 45;
                break;
            case 47:
                adjustedEventID = 46;
                break;
            case 49:
                adjustedEventID = 47;
                break;
            case 35:
                adjustedEventID = 48;
                break;
            case 37:
                adjustedEventID = 49;
                break;
            case 39:
                adjustedEventID = 50;
                break;
            case 41:
                adjustedEventID = 51;
                break;
            case 51:
                adjustedEventID = 52;
                break;
            case 53:
                adjustedEventID = 53;
                break;
            case 57:
                adjustedEventID = 54;
                break;
            case 59:
                adjustedEventID = 55;
                break;

        }
        System.out.println("Adjusted event id: "+ adjustedEventID);
        System.out.println("Original event id: "+ eID);
        return adjustedEventID;
    }
}
