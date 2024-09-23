package com.jobo.stats;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Base64;

@SpringBootApplication
public class StatsApplication {


	private static String accessToken; //variable para guardar token
	private static String refreshToken;

	public static void main(String[] args) {
		SpringApplication.run(StatsApplication.class, args);
	}

	@Controller
	public static class AuthController {

		@GetMapping("/login")
		public RedirectView login() {
			String clientId = "b4cda3d2795543e3977998f5d982bfc4";
			String redirectUri = "http://localhost:8080/callback";
			String scope = "user-read-private user-read-email";
			String authUrl = "https://accounts.spotify.com/authorize"
					+ "?response_type=code"
					+ "&client_id=" + clientId
					+ "&redirect_uri=" + redirectUri
					+ "&scope=" + scope;
			return new RedirectView(authUrl); // redirigir usuario a spotify
		}

		@GetMapping("/callback")
		public RedirectView callback(@RequestParam("code") String code) {
			try {
				spotifyAuth(code); // llama al metodo para recibir y guardar toker
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
			return new RedirectView("/"); // redirige a la pagina principal despues de la autenticación
		}

		@GetMapping("/error")
		public String errorPage() {
			return "Error en la solicitud. Inténtalo nuevamente.";
		}

		@GetMapping("/sanityCheck")
		@ResponseBody
		public String sanityCheck() {
			return "El servicio anda 100% barrani."; // mensaje de verificación sanity check
		}

		// endpoint para verificar el token guardado
		@GetMapping("/getToken")
		@ResponseBody
		public String getToken() {
			if (accessToken != null) {
				return "Token: " + accessToken;
			} else {
				return "No se guardo el token.";
			}
		}
	}

	// metodo para manejar auth y guardar token
	public static void spotifyAuth(String code) throws IOException, ParseException {
		// Credenciales de Spotify
		String clientId = "b4cda3d2795543e3977998f5d982bfc4";
		String clientSecret = "01b2415dd16b4b54b85980fb4cc7cc73";
		String redirectUri = "http://localhost:8080/callback";


		String auth = clientId + ":" + clientSecret;
		String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");
		post.setHeader("Authorization", "Basic " + encodedAuth);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		String body = "grant_type=authorization_code"
				+ "&code=" + code
				+ "&redirect_uri=" + redirectUri;
		post.setEntity(new StringEntity(body));

		try (CloseableHttpResponse response = httpClient.execute(post)) {
			int statusCode = response.getCode();
			String jsonResponse = EntityUtils.toString(response.getEntity());

			System.out.println("Status Code: " + statusCode);
			System.out.println("Response: " + jsonResponse);

			if (statusCode == 200) {

				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
				accessToken = jsonObject.get("access_token").getAsString(); // Guardar el access token
				refreshToken = jsonObject.has("refresh_token") ? jsonObject.get("refresh_token").getAsString() : null;

				// imprimir el token de acceso
				System.out.println("Token guardado: " + accessToken);
			} else {
				// manejar errores
				System.out.println("Error al obtener el token, respuesta: " + jsonResponse);
			}
		}
	}
}