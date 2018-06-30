package org.catchcase.cloudcomputing.webserver;

import org.catchcase.cloudcomputing.webserver.backend.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * class RequestController
 *
 * This class is the REST-mapped controller for the Spring web service which handles requests made to by the client to the rest endpoint. It delegates operations to the CloudStorageManager class and sends the appropriate HTTP Responses to the client for each request, depending on the operation outcome.
 */
@RequestMapping(value = "/")
@RestController
public class RequestController {

	@Autowired
	private CloudStorageManager manager = CloudStorageManager.getManagerInstance();

	@RequestMapping(method = RequestMethod.GET, produces = {MediaType.TEXT_PLAIN_VALUE})
	public String index() {
		return "CloudStorage Web Server - Group 2";
	}

	@RequestMapping(method = RequestMethod.POST, path = "/entries/{key}")
	public ResponseEntity<String> insert(@PathVariable String key, @RequestBody String value) {
		CloudStorageLogger result;

		try {
			result = manager.insert(Integer.parseInt(key), value);
		} catch (NumberFormatException nfe) {
			return ResponseEntity.badRequest().body("Invalid key parameter; must be integer");
		}

		return getStringResponseEntity(result);
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/entries/{key}")
	public ResponseEntity<String> delete(@PathVariable String key) {
		CloudStorageLogger result;

		try {
			result = manager.delete(Integer.parseInt(key));
		} catch (NumberFormatException nfe) {
			return ResponseEntity.badRequest().body("Invalid key parameter; must be integer");
		}

		return getStringResponseEntity(result);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/entries/{key}")
	public ResponseEntity<String> search(@PathVariable String key) {
		CloudStorageLogger result;

		try {
			result = manager.search(Integer.parseInt(key));
		} catch (NumberFormatException nfe) {
			return ResponseEntity.badRequest().body("Invalid key parameter; must be integer");
		}

		return getStringResponseEntity(result);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/entries/range/{startKey}/{endKey}")
	public ResponseEntity<String> range(
			@PathVariable("startKey") String startKey, @PathVariable("endKey") String endKey) {
		List<String> result;

		try {
			result = manager.rangeQuery(Integer.parseInt(startKey), Integer.parseInt(endKey));
		} catch (NumberFormatException nfe) {
			return ResponseEntity.badRequest().body("Invalid key parameter; must be integer");
		}

		if (result.size() == 0) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(String.join(", ", result));
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/entries")
	public ResponseEntity<String> retrieveAll() {
		List<String> result;

		try {
			result = manager.listAllEntries();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Something went wrong!");
		}

		if (result == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(String.join(", ", result));
		}
	}

	private ResponseEntity<String> getStringResponseEntity(CloudStorageLogger result) {
		if (result.isStatus()) {
			if (result.getMsg() != null)
				return ResponseEntity.ok(result.getMsg());
			else
				return ResponseEntity.ok("");
		} else {
			if (result.getMsg() != null)
				return ResponseEntity.badRequest().body(result.getMsg());
			else
				return ResponseEntity.badRequest().body("");
		}
	}
}