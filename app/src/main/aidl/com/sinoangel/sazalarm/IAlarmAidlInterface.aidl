// IAlarmAidlInterface.aidl
package com.sinoangel.sazalarm;

// Declare any non-default types here with import statements

interface IAlarmAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

  void setOnceAlarm(long id,long startTime);
  void setRepeatAlarm(long id,long startTime);
  void cancelAlarm(long id);
}
