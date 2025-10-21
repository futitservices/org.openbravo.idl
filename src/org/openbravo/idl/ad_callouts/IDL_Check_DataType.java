/*
 ************************************************************************************
 * Copyright (C) 2009-2020 Openbravo S.L.U.
 * Licensed under the Openbravo Commercial License version 1.0
 * You may obtain a copy of the License at http://www.openbravo.com/legal/obcl.html
 * or in the legal folder of this module distribution.
 ************************************************************************************
 */

package org.openbravo.idl.ad_callouts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;

import org.openbravo.erpCommon.ad_callouts.SimpleCallout;
import org.openbravo.erpCommon.utility.Utility;

public class IDL_Check_DataType extends SimpleCallout {

  @Override
  protected void execute(CalloutInfo info) throws ServletException {

    final String strChanged = info.getStringParameter("inpLastFieldChanged");
    if (log4j.isDebugEnabled()) {
      log4j.debug("CHANGED: " + strChanged);
    }
    final String strDefaultValue = info.getStringParameter("inpdefaultvalue");
    final String strDataType = info.getStringParameter("inpdatatype");

    boolean validation = true;
    String infoMsg = null;
    String result = "";

    if (!strDefaultValue.isEmpty()) {

      if (strDataType.equals("string")) {
        // No validation

      } else if (strDataType.equals("boolean")) {
        if (strDefaultValue.equalsIgnoreCase("TRUE") || strDefaultValue.equalsIgnoreCase("FALSE")) {
          // No validation
        } else {
          validation = false;
          infoMsg = "IDL_VALIDATION_BOOLEAN";
        }
      } else if (strDataType.equals("numeric")) {
        try {
          Double.parseDouble(strDefaultValue);
        } catch (NumberFormatException nfe) {
          validation = false;
          if (strDefaultValue.contains(",")) {
            infoMsg = "IDL_VALIDATION_COMMA";
          }
        }
      } else if (strDataType.equals("date")) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date testDate = null;
        try {
          testDate = sdf.parse(strDefaultValue);
        } catch (ParseException e) {
          testDate = new Date();

        }
        if (!sdf.format(testDate).equals(strDefaultValue)) {
          validation = false;
          infoMsg = "IDL_VALIDATION_DATE";
        }
      }

      if (!validation) {
        result = Utility.messageBD(this, "IDL_VALIDATION_FAIL", info.vars.getLanguage()) + "<br>"
            + Utility.messageBD(this, "IDL_VALIDATION_VALUE", info.vars.getLanguage())
            + strDefaultValue + "<br>"
            + Utility.messageBD(this, "IDL_VALIDATION_TYPE", info.vars.getLanguage()) + strDataType
            + ((infoMsg == null) ? ""
                : ("<br>" + Utility.messageBD(this, infoMsg, info.vars.getLanguage())));
      }
    }

    if (!result.isEmpty()) {
      info.addResult("INFO", result);
    }
  }
}
