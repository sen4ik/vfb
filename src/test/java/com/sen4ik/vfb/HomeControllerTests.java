package com.sen4ik.vfb;

import com.sen4ik.vfb.controllers.HomeController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = HomeController.class)
public class HomeControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void main() throws Exception {
		ResultActions resultActions = mockMvc.perform(get("/home"))
				.andExpect(status().isOk())
				.andExpect(view().name("home"))
				.andExpect(model().attribute("message", equalTo("This is test message")))
				.andExpect(content().string(containsString("Hello, This is test message")));

		MvcResult mvcResult = resultActions.andReturn();
		ModelAndView mv = mvcResult.getModelAndView();
	}

}
