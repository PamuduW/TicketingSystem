import axios from "axios";

const API = axios.create({
    // baseURL: "http://localhost:8080/api",
    baseURL: "https://ticketing---system-32a1f2f59169.herokuapp.com/api",
});

/**
 * Axios instance configured with the base URL for the API.
 * This instance can be used to make HTTP requests to the server.
 */
export default API;