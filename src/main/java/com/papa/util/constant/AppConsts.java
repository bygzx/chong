package com.papa.util.constant;

/**
 * @author bygzx
 * @date 2019/5/14 14:30
 **/
public class AppConsts {

    public enum AppSource {
        UNKNOWN(0),
        /** 渠道1 */
        CHANNEL_1(1),
        /** 渠道2 */
        CHANNEL_2(2),
        /** 渠道3 */
        CHANNEL_3(3),
        ;

        private int val;
        AppSource(int val) {
            this.val = val;
        }

        public int getVal() {
            return this.val;
        }

        /**
         * @param val
         * @return not null
         */
        public static AppSource valueOf(int val) {
            AppSource[] vals = AppSource.values();
            if (val < 0 || val > vals.length) {
                return UNKNOWN;
            }
            return vals[val];
        }
    }



}
