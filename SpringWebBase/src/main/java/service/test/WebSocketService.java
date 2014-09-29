package service.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("socket")
public class WebSocketService {
	@RequestMapping(value = "test", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> upload() {
		Map<String,Object> result=new HashMap<String, Object>();
		result.put("a", 123);
		return result;
	}
}
