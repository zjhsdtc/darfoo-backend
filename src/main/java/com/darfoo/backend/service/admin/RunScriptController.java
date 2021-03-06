package com.darfoo.backend.service.admin;

import com.darfoo.backend.utils.RunShellUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by zjh on 15-1-11.
 */

@Controller
@RequestMapping("/admin/runscript")
public class RunScriptController {
    @RequestMapping(value = "/resourcevolumn", method = RequestMethod.GET)
    public String getResourceVolumn(ModelMap modelMap) {
        String scriptpath = "./resourcevolumn.sh";
        String volumn = RunShellUtils.runshellscriptwithresult(scriptpath);
        modelMap.put("volumn", volumn);
        return "shell/showvolumn";
    }
}
