package com.example.backend.service;

// none of this is mine!
// kudos to the original creator of this code: https://www.codeproject.com/Tips/1068755/Quick-card-Poker-Ranking-in-Java

import java.util.EnumSet;
import java.util.HashMap;

public class RankPokerHandPublic {

    public enum Combination {

        ROYAL_FLUSH(11),
        STRAIGHT_FLUSH(10),
        SKIP_STRAIGHT_FLUSH_ACE_LOW_TMP(9),
        FOUR_OF_A_KIND(8),
        FULL_HOUSE(7),
        FLUSH(6),
        STRAIGHT(5),
        SKIP_STRAIGHT_ACE_LOW_TMP(4),
        THREE_OF_A_KIND(3),
        TWO_PAIR(2),
        PAIR(1),
        HIGH_CARD(0);

        private final int rank;

        private final static HashMap<Integer, Combination> fromRank = new HashMap<Integer, Combination>();

        static {
            for (Combination combination: EnumSet.allOf(Combination.class)) {
                fromRank.put(combination.rank(), combination);
            }
        }

        private Combination(int rank) {
            this.rank = rank;
        }

        public int rank() {
            return rank;
        }

        public static Combination fromRank(int rank) {
            return fromRank.get(rank);
        }
    }

    private final static int[] hands={Combination.FOUR_OF_A_KIND.rank(), Combination.STRAIGHT_FLUSH.rank(), Combination.STRAIGHT.rank(),
            Combination.FLUSH.rank(), Combination.HIGH_CARD.rank(), Combination.PAIR.rank(), Combination.TWO_PAIR.rank(),
            Combination.ROYAL_FLUSH.rank(), Combination.THREE_OF_A_KIND.rank(), Combination.FULL_HOUSE.rank()};

    public final static int A=14, K=13, Q=12, J=11;
    public final static int[] suits5 = new int[] {1,2,4,8};

    private final static long MASK4_1 = 0b11110000_11110000_11110000_11110000_11110000_11110000_11110000_11110000L;
    private final static long MASK4_2 = 0b00001111_00001111_00001111_00001111_00001111_00001111_00001111_00001111L;

    /*
     * Get an int that is bigger as the hand is bigger, breaking ties and keeping actual (real) ties.
     * Apply >>26 to the returned value afterwards, to get the rank of the Combination (0..11)
     *
     * Based and improved on:
     * http://www.codeproject.com/Articles/569271/A-Poker-hand-analyzer-in-JavaScript-using-bit-math
     *
     * nr is an array of length 5, where each number should be 2..14.
     * suit is an array of length 5, where each number should be 1,2,4 or 8.
     */
    public static int rankPokerHand5(int[] nr, int[] suit) {
        long v = 0L;
        int set = 0;
        for (int i=0; i<5; i++) {
            v += (v&(15L<<(nr[i]*4))) + (1L<<(nr[i]*4));
            set |= 1 << (nr[i]-2);
        }
        int value = (int) (v % 15L - ((hasStraight(set)) || (set == 0x403c/4) ? 3L : 1L)); // keep the v value at this point
        value -= (suit[0] == (suit[1]|suit[2]|suit[3]|suit[4]) ? 1 : 0) * ((set == 0x7c00/4) ? -5 : 1);
        value = hands[value];

        // break ties
        value = value << 26;
        value |= value == Combination.FULL_HOUSE.rank()<<26 ? 64-Long.numberOfLeadingZeros(v & (v<<1) & (v<<2)) << 20
                : set == 0x403c/4 ? 0 // Ace low straights
                : ((64-Long.numberOfLeadingZeros(
                max((v&MASK4_1) & ((v&MASK4_1)<<1), (v&MASK4_2) & ((v&MASK4_2)<<1))) << 20) |
                (Long.numberOfTrailingZeros(
                        minPos((v&MASK4_1) & ((v&MASK4_1)<<1), (v&MASK4_2) & ((v&MASK4_2)<<1))) << 14));
        value |= set;
        return value;
    }

    private static long minPos(long a, long b) {
        return a == 0 ? b : b == 0 ? a : a < b ? a : b;
    }

    private static long max(long a, long b) {
        return a > b ? a : b;
    }

    public static void initRankPokerHand7() {
        for (int i=0; i<32768; i++) {
            straightFlush[i] = getStraight(i);
            if ((i&(0x403c/4))==0x403c/4) {
                straightFlush[i] |= 1<<3;
            }
            flush[i] = Integer.bitCount(i) >= 5 ? getUpperFive(i) : 0;
        }
        int max=0;
        int count = 0;
        int[] nrs = {1,2,4,8,1};
        for (int nr1=2; nr1<=14; nr1++) {
            for (int nr2=nr1; nr2<=14; nr2++) {
                for (int nr3=nr2; nr3<=14; nr3++) {
                    for (int nr4=nr3; nr4<=14; nr4++) {
                        for (int nr5=nr4; nr5<=14; nr5++) {
                            if (nr1==nr5) {
                                continue;
                            }
                            for (int nr6=nr5; nr6<=14; nr6++) {
                                if (nr2==nr6) {
                                    continue;
                                }
                                for (int nr7=nr6; nr7<=14; nr7++) {
                                    if (nr3==nr7) {
                                        continue;
                                    }
                                    int[][] hands = get5HandsFrom7(new int[]{nr1,nr2,nr3,nr4,nr5,nr6,nr7});
                                    int maxValue = 0;
                                    int straight = 0;
                                    for (int[] hand: hands) {
                                        int value = rankPokerHand5(hand, nrs);
                                        int set = value & ((1<<14)-1);
                                        straight |= (hasStraight(set) || set == 0x403c/4) ? set : 0;
                                        maxValue = value>maxValue ? value : maxValue;
                                    }
                                    int sum = recurrence[nr1-2] + recurrence[nr2-2] + recurrence[nr3-2] + recurrence[nr4-2] + recurrence[nr5-2] + recurrence[nr6-2] + recurrence[nr7-2];
                                    count++;
                                    lookup[sum%4731770] = maxValue;
                                    if (sum%4731770>max) {
                                        max = sum%4731770; //7561824
                                    }
                                    RankPokerHandPublic.straight[sum%4731770] = straight;
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("count (lower bound):" + count);
        System.out.println("MAX:" + max);
    }

    private static int totalLength=0;

    final private static int[] COUNTS = new int[] {1, 13, 78, 286, 715, 1287, 1716, 1716};

    private static int getUpperFive(int cards) {
        cards = Integer.bitCount(cards) > 5 ? cards & ~((1<<Integer.numberOfTrailingZeros(cards)+1)-1) : cards;
        cards = Integer.bitCount(cards) > 5 ? cards & ~((1<<Integer.numberOfTrailingZeros(cards)+1)-1) : cards;
        return cards;
    }

    private static final int[] recurrence = {
            0, 1, 5, 24, 106, 472, 2058, 8768, 29048, 70347, 233028, 583164, 1472911
    };

    private static int[] lookup = new int[4731770];
    private final static int[] straight = new int[4731770];
    private final static int[] flush = new int[32768];
    private final static int[] straightFlush = new int[32768];

    /**
     * @param nr array of length 7 with card rankings 0..12 inclusive (0=Two, 12=Ace)
     * @param suit array of length 7 with suits 0..3 inclusive
     * @param buffer buffer array of length 4 that's used by this method internally. Values can contain garbage and will
     *              be overwritten. Caller should reuse this array on multiple calls to this method for best
     *              performance.
     * @return int indicating the relative strength of this hand.
     */
    public static int rankPokerHand7(int[] nr, int[] suit, int[] buffer) {
        int index=0;
        for (int i=0; i<4; i++) {
            buffer[i] = 0;
        }
        for (int i=0; i<7; i++) {
            buffer[suit[i]] |= 1L << nr[i];
            index += recurrence[nr[i]];
        }
        index = index % 4731770;
        int value = lookup[index];
        int fl = 0;
        for (int i=0; i<4; i++) {
            fl |= flush[buffer[i]];
        }
        int str = straight[index];

        int straightFl = fl==0 ? 0 :
                (straightFlush[str&buffer[0]] | straightFlush[str&buffer[1]] | straightFlush[str&buffer[2]] | straightFlush[str&buffer[3]]);

        straightFl = Integer.highestOneBit(straightFl);

        return straightFl == 1 << 12 ? (Combination.ROYAL_FLUSH.rank() << 26)
                : straightFl != 0 ? (Combination.STRAIGHT_FLUSH.rank() << 26) | straightFl
                : fl != 0 ? (Combination.FLUSH.rank() << 26) | fl
                : value;
    }

    private static boolean hasStraight(int set) {
        return 0 != (set&(set>>1)&(set>>2)&(set>>3)&(set>>4));
    }

    private static int getStraight(int set) {
        return set&(set<<1)&(set<<2)&(set<<3)&(set<<4);
    }

    public static void main(String[] args) {

        int count = 0;
        for (int i=0; i<128; i++) {
            if (Integer.bitCount(i)==3) {
                count++;
            }
        }

        System.out.println(count);
        System.out.println(14*14*14*14*13*13*13/count);

        test(rankPokerHand5(new int[]{10, J, A, K, Q}, suits(new int[]{1, 1, 1, 1, 1  }))); // Royal Flush
        test(rankPokerHand5(new int[]{ 4, 5, 6, 7, 8}, suits(new int[]{1, 1, 1, 1, 1  }))); // Straight Flush
        test(rankPokerHand5(new int[]{ 4, 5, 6, 7, 3}, suits(new int[]{1, 1, 1, 1, 1  }))); // Straight Flush
        test(rankPokerHand5(new int[]{ 4, 5, 6, 3, 2}, suits(new int[]{1, 1, 1, 1, 1  }))); // Straight Flush
        test(rankPokerHand5(new int[]{ 2, 3, 4, 5, A}, suits(new int[]{1, 1, 1, 1, 1  }))); // Straight Flush (ace low)
        test(rankPokerHand5(new int[]{ 8, 8, 8, 8, 9}, suits(new int[]{1, 2, 3, 0, 1  }))); // 4 of a Kind
        test(rankPokerHand5(new int[]{ 8, 8, 8, 8, 7}, suits(new int[]{1, 2, 3, 0, 1  }))); // 4 of a Kind
        test(rankPokerHand5(new int[]{ 7, 7, 7, A, 7}, suits(new int[]{1, 2, 3, 0, 1  }))); // 4 of a Kind
        test(rankPokerHand5(new int[]{ 7, 7, 7, 9, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // Full house
        test(rankPokerHand5(new int[]{ 7, 7, 7, 8, 8}, suits(new int[]{1, 2, 3, 1, 2  }))); // Full house
        test(rankPokerHand5(new int[]{ 6, 6, 6, A, A}, suits(new int[]{1, 2, 3, 1, 2  }))); // Full house
        test(rankPokerHand5(new int[]{10, J, 6, K, 9}, suits(new int[]{2, 2, 2, 2, 2  }))); // Flush
        test(rankPokerHand5(new int[]{10, J, 6, 3, 9}, suits(new int[]{2, 2, 2, 2, 2  }))); // Flush
        test(rankPokerHand5(new int[]{10, J, 5, 3, 9}, suits(new int[]{2, 2, 2, 2, 2  }))); // Flush
        test(rankPokerHand5(new int[]{10, J, Q, K, A}, suits(new int[]{1, 2, 3, 2, 0  }))); // Straight
        test(rankPokerHand5(new int[]{10, J, Q, K, 9}, suits(new int[]{1, 2, 3, 2, 0  }))); // Straight
        test(rankPokerHand5(new int[]{10, J, Q, 8, 9}, suits(new int[]{1, 2, 3, 2, 0  }))); // Straight
        test(rankPokerHand5(new int[]{ 2, 3, 4, 5, A}, suits(new int[]{1, 2, 3, 2, 0  }))); // Straight (ace low)
        test(rankPokerHand5(new int[]{ 4, 4, 4, 8, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 3 of a Kind
        test(rankPokerHand5(new int[]{ 4, 4, 4, 8, 7}, suits(new int[]{1, 2, 3, 1, 2  }))); // 3 of a Kind
        test(rankPokerHand5(new int[]{ 3, 3, 3, K, A}, suits(new int[]{1, 2, 3, 1, 2  }))); // 3 of a Kind
        test(rankPokerHand5(new int[]{ 3, 3, 3, 6, 5}, suits(new int[]{1, 2, 3, 1, 2  }))); // 3 of a Kind
        test(rankPokerHand5(new int[]{ 8, 8, J, 9, 9}, suits(new int[]{0, 2, 3, 1, 3  }))); // 2 Pair
        test(rankPokerHand5(new int[]{ 8, 8,10, 9, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 2 Pair
        test(rankPokerHand5(new int[]{ 7, 7, Q, 9, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 2 Pair
        test(rankPokerHand5(new int[]{ 7, 7, 6, 9, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 2 Pair
        test(rankPokerHand5(new int[]{ 6, 6, A, 8, 8}, suits(new int[]{1, 2, 3, 1, 2  }))); // 2 Pair
        test(rankPokerHand5(new int[]{ 8, 8, 3, 5, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 1 Pair
        test(rankPokerHand5(new int[]{ 7, 7, 3, 5, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 1 Pair
        test(rankPokerHand5(new int[]{ 7, 7, 3, 2, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 1 Pair
        test(rankPokerHand5(new int[]{10, 5, 4, 7, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // High Card
        test(rankPokerHand5(new int[]{10, 5, 3, 7, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // High Card
        System.out.println("Wrongs: " + testWrongs);

        System.out.print("Setting up lookup tables... ");
        long startTime = System.currentTimeMillis();
        initRankPokerHand7();

        int[] rank = {0, 0, 0, 1, 0, 12, 12};
        int[] suit = {0, 1, 2, 0, 3, 0, 1};

        int[] rank2 = {0, 0, 0, 1, 0, 8, 8};
        int[] suit2 = {0, 1, 2, 0, 3, 0, 1};

        int[] rank3 = {0, 0, 0, 5, 0, 7, 7};
        int[] suit3 = {0, 1, 2, 0, 3, 0, 1};

        int[] buffer = new int[4];

        System.out.println("four-of-a-kind 2's with A kicker: " + rankPokerHand7(rank, suit, buffer));
        System.out.println("four-of-a-kind 2's with 10 kicker: " + rankPokerHand7(rank2, suit2, buffer));
        System.out.println("four-of-a-kind 2's with 9 kicker: " + rankPokerHand7(rank3, suit3, buffer));

        long runTime = System.currentTimeMillis() - startTime;
        System.out.println(runTime + " ms");

        System.out.println("totalLength = " + totalLength);

        System.out.println("checksum: " + count7()); // warmup
        handToCount = new int[12];
        startTime = System.currentTimeMillis();
        int checksum = count7();
        runTime = System.currentTimeMillis() - startTime;
        long countHands = printHandToCount();
        System.out.println(runTime + " ms; " + (countHands / runTime / 1000L) + " MH/s; checksum: " + checksum);

        System.out.println("AK vs 22 preflop:");
        System.out.println(rangeVsRange(new int[] {11,12,-1,-1,-1,-1,-1,0,0}, new int[] {0,1,-1,-1,-1,-1,-1,0,1}));
        System.out.println("22 vs AK preflop:");
        System.out.println(rangeVsRange(new int[] {0,0,-1,-1,-1,-1,-1,11,12}, new int[] {0,1,-1,-1,-1,-1,-1,0,1}));
        System.out.println("22 vs AK preflop:");
        System.out.println(testRangeVsRange(0, 0, 0, 1, 11, 0, 12, 1));
        System.out.println("AK vs 22 preflop:");
        System.out.println(testRangeVsRange(12, 1, 11, 0, 0, 0, 0, 1));
        System.out.println("AK vs QQ preflop:");
        System.out.println(rangeVsRange(new int[] {12,11,-1,-1,-1,-1,-1,10,10}, new int[] {0,1,-1,-1,-1,-1,-1,0,2}));
        System.out.println("AK vs QQ with flop: AKQ:");
        System.out.println(rangeVsRange(new int[] {12,11,12,11,10,-1,-1,10,10}, new int[] {0,1,1,0,1,-1,-1,0,2}));
        System.out.println("Pair of A on the flow vs random opponent");
        System.out.println(rangeVsRange(new int[] {5,12,12,1,3,-1,-1,-1,-1}, new int[] {0,0,1,1,2,-1,-1,-1,-1}));
        System.out.println("Postflop: 8s8c vs KsJs on AsAc5d");
        System.out.println(rangeVsRange(new int[]{6, 6, 12, 12, 3, -1, -1, 11, 9}, new int[]{0, 1, 0, 1, 2, -1, -1, 0, 0}));
        //System.out.println("Pair of 10's preflop against one player with random cards:"); // takes too long
        //System.out.println(rangeVsRange(new int[] {8,8,-1,-1,-1,-1,-1,-1,-1}, new int[] {0,1,-1,-1,-1,-1,-1,-1,-1}));
        //System.out.println("All two-player combinations"); // takes almost forever
        //System.out.println(rangeVsRange(new int[] {-1,-1,-1,-1,-1,-1,-1,-1,-1}, new int[] {-1,-1,-1,-1,-1,-1,-1,-1,-1}));
    }

    private static int[] suits(int[] is) {
        int[] s = new int[is.length];
        for (int i=0; i<is.length; i++) {
            s[i] = suits5[is[i]];
        }
        return s;
    }

    private static long previousTestValue = Long.MAX_VALUE;
    private static int testWrongs = 0;

    private static void test(int rankPokerHand) {
        System.out.println(rankPokerHand + ": " + Combination.fromRank(rankPokerHand >> 26));
        if (rankPokerHand >= previousTestValue) {
            System.out.println("WRONG"); // should not happen
            testWrongs++;
        } else {
            previousTestValue = rankPokerHand;
        }
    }


    private static long printHandToCount() {
        long sum = 0;
        for (int count: handToCount){
            sum += count;
        }
        System.out.println("total hands: " + sum);
        for (int rank=handToCount.length-1; rank>=0; rank--) {
            System.out.println(rank + ": " + Combination.fromRank(rank) + " => " + handToCount[rank] + " (" + handToCount[rank]/((float)sum)*100 + "%)");
        }
        return sum;
    }

    static int[] handToCount = new int[12];

    public static int[][] get5HandsFrom7(int[] nrs) {
        int[][] result = new int[21][];
        int index = 0;
        for (int x1=0; x1<7; x1++) {
            for (int x2=x1+1; x2<7; x2++) {
                for (int x3=x2+1; x3<7; x3++) {
                    for (int x4=x3+1; x4<7; x4++) {
                        for (int x5=x4+1; x5<7; x5++) {
                            result[index] = new int[] {nrs[x1], nrs[x2], nrs[x3], nrs[x4], nrs[x5]};
                            index++;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static int count7() {
        int checksum = 0;
        int[] nr = new int[7];
        int[] suit = new int[7];
        int[] buffer = new int[4];
        for (int card1=0; card1<52; card1++) {
            suit[0] = card1%4;
            nr[0] = card1/4 ;
            for (int card2=card1+1; card2<52; card2++) {
                suit[1] = card2%4;
                nr[1] = card2/4 ;
                for (int card3=card2+1; card3<52; card3++) {
                    suit[2] = card3%4;
                    nr[2] = card3/4 ;
                    for (int card4=card3+1; card4<52; card4++) {
                        suit[3] =card4%4;
                        nr[3] = card4/4 ;
                        for (int card5=card4+1; card5<52; card5++) {
                            suit[4] = card5%4;
                            nr[4] = card5/4 ;
                            for (int card6=card5+1; card6<52; card6++) {
                                suit[5] = card6%4;
                                nr[5] = card6/4 ;
                                for (int card7=card6+1; card7<52; card7++) {
                                    suit[6] = card7%4;
                                    nr[6] = card7/4 ;
                                    int rank = rankPokerHand7(nr, suit, buffer);
                                    handToCount[rank >> 26]++;
                                    checksum += rank;
                                }
                            }
                        }
                    }
                }
            }
        }
        return checksum;
    }

    /**
     * cards[0] = player 1 first hole card (0..12)
     * suits[0] = player 1 first hole suit (0..3)
     * cards[1] = player 1 second hole card (0..12)
     * suits[1] = player 1 second hole suit (0..3)
     * cards[i>=2, i<=6] = card on flop/turn/river (0..12) or -1 if not dealt yet
     * suits[i>=2, i<=6] = suit on flop/turn/river (0..12) or -1 if not dealt yet
     * cards[7] = player 2 first hole card (0..12) or -1 if unknown
     * suits[7] = player 2 first hole suit (0..3) or -1 if unknown
     * cards[8] = player 2 second hole card (0..12) or -1 if unknown
     * suits[8] = player 2 second hole suit (0..3) or -1 if unknown
     *
     * for any i (0<=i<=8): if cards[i]==-1 then it must be that suits[i]==-1
     */
    public static RangeResult rangeVsRange(int[] cards, int[] suits) {
        RangeResult rangeResult = new RangeResult();
        int[] nr = new int[7];
        int[] suit = new int[7];
        for (int i=0; i<7; i++) {
            nr[i] = -1;
            suit[i] = -1;
        }
        int newNr, newSuit;
        int[] buffer = new int[4];
        for (int card1=0; card1<52 || cards[0] != -1; card1++) {
            newNr = cards[0] != -1 ? cards[0] : card1 / 4;
            newSuit = suits[0] != -1 ? suits[0] : card1 % 4;
            if (cards[0] == -1 && duplicate(newNr, newSuit, nr, suit)) {
                if (cards[0] != -1) {
                    break;
                }
                continue;
            }
            nr[0] = newNr; int keepCard1 = newNr;
            suit[0] = newSuit; int keepSuit1 = newSuit;
            for (int card2 = 0; card2 < 52 || cards[1] != -1; card2++) {
                if (cards[1] == -1 && cards[0] == -1 && card2 <= card1) {
                    card2 = card1;
                    continue;
                }
                newNr = cards[1] != -1 ? cards[1] : card2 / 4;
                newSuit = suits[1] != -1 ? suits[1] : card2 % 4;
                if (duplicate(newNr, newSuit, nr, suit) || (cards[1] == -1 && duplicate(newNr, newSuit, cards, suits))) {
                    if (cards[1] != -1) {
                        break;
                    }
                    continue;
                }
                nr[1] = newNr; int keepCard2 = newNr;
                suit[1] = newSuit; int keepSuit2 = newSuit;
                for (int card3 = 0; card3 < 52 || cards[2] != -1; card3++) {
                    newNr = cards[2] != -1 ? cards[2] : card3 / 4;
                    newSuit = suits[2] != -1 ? suits[2] : card3 % 4;
                    if (duplicate(newNr, newSuit, nr, suit) || (cards[2] == -1 && duplicate(newNr, newSuit, cards, suits))) {
                        if (cards[2] != -1) {
                            break;
                        }
                        continue;
                    }
                    nr[2] = newNr;
                    suit[2] = newSuit;
                    for (int card4 = 0; card4 < 52 || cards[3] != -1; card4++) {
                        if (cards[3] == -1 && cards[2] == -1 && card4 <= card3) {
                            card4 = card3;
                            continue;
                        }
                        newNr = cards[3] != -1 ? cards[3] : card4 / 4;
                        newSuit = suits[3] != -1 ? suits[3] : card4 % 4;
                        if (duplicate(newNr, newSuit, nr, suit) || (cards[3] == -1 && duplicate(newNr, newSuit, cards, suits))) {
                            if (cards[3] != -1) {
                                break;
                            }
                            continue;
                        }
                        nr[3] = newNr;
                        suit[3] = newSuit;
                        for (int card5 = 0; card5 < 52 || cards[4] != -1; card5++) {
                            if (cards[4] == -1 && cards[3] == -1 && card5 <= card4) {
                                card5 = card4;
                                continue;
                            }
                            newNr = cards[4] != -1 ? cards[4] : card5 / 4;
                            newSuit = suits[4] != -1 ? suits[4] : card5 % 4;
                            if (duplicate(newNr, newSuit, nr, suit) || (cards[4] == -1 && duplicate(newNr, newSuit, cards, suits))) {
                                if (cards[4] != -1) {
                                    break;
                                }
                                continue;
                            }
                            nr[4] = newNr;
                            suit[4] = newSuit;
                            for (int card6 = 0; card6 < 52 || cards[5] != -1; card6++) {
                                if (cards[5] == -1 && cards[4] == -1 && card6 <= card5) {
                                    card6 = card5;
                                    continue;
                                }
                                newNr = cards[5] != -1 ? cards[5] : card6 / 4;
                                newSuit = suits[5] != -1 ? suits[5] : card6 % 4;
                                if (duplicate(newNr, newSuit, nr, suit) || (cards[5] == -1 && duplicate(newNr, newSuit, cards, suits))) {
                                    if (cards[5] != -1) {
                                        break;
                                    }
                                    continue;
                                }
                                nr[5] = newNr;
                                suit[5] = newSuit;
                                for (int card7 = 0; card7 < 52 || cards[6] != -1; card7++) {
                                    if (cards[6] == -1 && cards[5] == -1 && card7 <= card6) {
                                        card7 = card6;
                                        continue;
                                    }
                                    newNr = cards[6] != -1 ? cards[6] : card7 / 4;
                                    newSuit = suits[6] != -1 ? suits[6] : card7 % 4;
                                    if (duplicate(newNr, newSuit, nr, suit) || (cards[6] == -1 && duplicate(newNr, newSuit, cards, suits))) {
                                        if (cards[6] != -1) {
                                            break;
                                        }
                                        continue;
                                    }
                                    nr[6] = newNr;
                                    suit[6] = newSuit;

                                    int score1 = rankPokerHand7(nr, suit, buffer);

                                    for (int card8 = 0; card8 < 52 || cards[7] != -1; card8++) {
                                        newNr = cards[7] != -1 ? cards[7] : card8 / 4;
                                        newSuit = suits[7] != -1 ? suits[7] : card8 % 4;
                                        if (duplicate(newNr, newSuit, nr, suit) || (cards[7] == -1 && duplicate(newNr, newSuit, cards, suits))) {
                                            if (cards[7] != -1) {
                                                break;
                                            }
                                            continue;
                                        }
                                        nr[0] = newNr;
                                        suit[0] = newSuit;
                                        for (int card9 = 0; card9 < 52 || cards[8] != -1; card9++) {
                                            if (cards[8] == -1 && cards[7] == -1 && card9 <= card8) {
                                                continue;
                                            }
                                            newNr = cards[8] != -1 ? cards[8] : card9 / 4;
                                            newSuit = suits[8] != -1 ? suits[8] : card9 % 4;
                                            if ((newNr == keepCard1 && newSuit == keepSuit1) || duplicate(newNr, newSuit, nr, suit) || (cards[8] == -1 && duplicate(newNr, newSuit, cards, suits))) {
                                                if (cards[8] != -1) {
                                                    break;
                                                }
                                                continue;
                                            }
                                            nr[1] = newNr;
                                            suit[1] = newSuit;

                                            int score2 = rankPokerHand7(nr, suit, buffer);

                                            rangeResult.process(score1, score2);

                                            nr[1] = keepCard2;
                                            suit[1] = keepSuit2;
                                            if (cards[8] != -1) {
                                                break;
                                            }
                                        }
                                        nr[0] = keepCard1;
                                        suit[0] = keepSuit1;
                                        if (cards[7] != -1) {
                                            break;
                                        }
                                    }
                                    if (cards[6] != -1) {
                                        break;
                                    }
                                }
                                nr[6] = -1;
                                suit[6] = -1;
                                if (cards[5] != -1) {
                                    break;
                                }
                            }
                            nr[5] = -1;
                            suit[5] = -1;
                            if (cards[4] != -1) {
                                break;
                            }
                        }
                        nr[4] = -1;
                        suit[4] = -1;
                        if (cards[3] != -1) {
                            break;
                        }
                    }
                    nr[3] = -1;
                    suit[3] = -1;
                    if (cards[2] != -1) {
                        break;
                    }
                }
                nr[2] = -1;
                suit[2] = -1;
                if (cards[1] != -1) {
                    break;
                }
            }
            nr[1] = -1;
            suit[1] = -1;
            if (cards[0] != -1) {
                break;
            }
        }
        return rangeResult;
    }

    private static RangeResult testRangeVsRange(int nr1, int suit1, int nr2, int suit2, int nr3, int suit3, int nr4, int suit4) {

        //SevenCardEvaluator alternativeEvaluator = new SevenCardEvaluator();

        RangeResult rangeResult = new RangeResult();
        int[] nr = new int[7];
        int[] suit = new int[7];
        int newNr, newSuit;
        int[] buffer = new int[4];
        for (int card2=0; card2<52; card2++) {
            newNr = card2 / 4;
            newSuit = card2 % 4;
            if ((newNr == nr1 && newSuit == suit1) || (newNr == nr2 && newSuit == suit2) || (newNr == nr3 && newSuit == suit3) || (newNr == nr4 && newSuit == suit4)) {
                continue;
            }
            nr[2] = newNr;
            suit[2] = newSuit;
            for (int card3=card2+1; card3<52; card3++) {
                newNr = card3 / 4;
                newSuit = card3 % 4;
                if ((newNr == nr1 && newSuit == suit1) || (newNr == nr2 && newSuit == suit2) || (newNr == nr3 && newSuit == suit3) || (newNr == nr4 && newSuit == suit4)) {
                    continue;
                }
                nr[3] = newNr;
                suit[3] = newSuit;
                for (int card4=card3+1; card4<52; card4++) {
                    newNr = card4 / 4;
                    newSuit = card4 % 4;
                    if ((newNr == nr1 && newSuit == suit1) || (newNr == nr2 && newSuit == suit2) || (newNr == nr3 && newSuit == suit3) || (newNr == nr4 && newSuit == suit4)) {
                        continue;
                    }
                    nr[4] = newNr;
                    suit[4] = newSuit;
                    for (int card5=card4+1; card5<52; card5++) {
                        newNr = card5 / 4;
                        newSuit = card5 % 4;
                        if ((newNr == nr1 && newSuit == suit1) || (newNr == nr2 && newSuit == suit2) || (newNr == nr3 && newSuit == suit3) || (newNr == nr4 && newSuit == suit4)) {
                            continue;
                        }
                        nr[5] = newNr;
                        suit[5] = newSuit;
                        for (int card6=card5+1; card6<52; card6++) {
                            newNr = card6 / 4;
                            newSuit = card6 % 4;
                            if ((newNr == nr1 && newSuit == suit1) || (newNr == nr2 && newSuit == suit2) || (newNr == nr3 && newSuit == suit3) || (newNr == nr4 && newSuit == suit4)) {
                                continue;
                            }
                            nr[6] = newNr;
                            suit[6] = newSuit;

                            nr[0] = nr1;
                            suit[0] = suit1;
                            nr[1] = nr2;
                            suit[1] = suit2;

                            int score1 = rankPokerHand7(nr, suit, buffer);

                            int[] nrs1 = new int[] {
                                    nr[0], nr[1], nr[2], nr[3], nr[4], nr[5], nr[6]};
                            int[] suits1 = new int[] {
                                    suit[0], suit[1], suit[2], suit[3], suit[4], suit[5], suit[6]};

                            nr[0] = nr3;
                            suit[0] = suit3;
                            nr[1] = nr4;
                            suit[1] = suit4;

                            int score2 = rankPokerHand7(nr, suit, buffer);

                            rangeResult.process(score1, score2);

                            //if (alternativeEvaluator.compare(aceIs1(nrs1), suits1, aceIs1(nr), suit) != Integer.compare(score1, score2)) {
                            //    throw new RuntimeException("disagreement");
                            //}
                        }
                    }
                }
            }
        }
        return rangeResult;
    }

    private static int[] aceIs1(int[] nrs) {
        int[] result = new int[nrs.length];
        for (int i=0; i<nrs.length; i++) {
            result[i] = 1+((nrs[i]+1)%13);
        }
        return result;
    }

    private static boolean duplicate(int card, int suit, int[] cards, int[] suits) {
        for (int i=0; i<cards.length; i++) {
            if (card==cards[i] && suit==suits[i]) {
                return true;
            }
        }
        return false;
    }

    public static class RangeResult {
        public long win;
        public long draw;
        public long loss;

        public void process(int score1, int score2) {
            if (score1 > score2) {
                win++;
            } else if (score1 < score2) {
                loss++;
            } else {
                draw++;
            }
        }

        public String toString() {
            double eq = ((double)win + 0.5d*(double)draw) / (double)(win+loss+draw);
            return "win: " + win + "; loss: " + loss + "; draw: " + draw + "; eq: " + eq;
        }
    }
}