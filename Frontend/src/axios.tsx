import axios from "axios";

const API = axios.create({
    // baseURL: "http://localhost:8080/api",
    baseURL: "https://ticketing---system-32a1f2f59169.herokuapp.com/api",
});

export default API;
