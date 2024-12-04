import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../../axios.tsx";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Switch from "@mui/material/Switch";

const Register: React.FC = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isVendor, setIsVendor] = useState(true);
    const navigate = useNavigate();

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        const data = {
            username: username,
            pass: password,
        };
        const url = isVendor ? "/vendor" : "/customer";
        console.log("Sending params:", data); // Log the params being sent
        try {
            const response = await API.post(url, data);
            console.log("Response:", response.data);
            alert("Going back to login...");
            setTimeout(() => {
                navigate("/");
            }, 1500); // Wait for 2 seconds before redirecting
        } catch (error) {
            console.error("Error:", error);
        }
    };

    const handleBackToLogin = () => {
        navigate("/");
    };

    return (
        <form onSubmit={handleSubmit}>
            <Switch
                checked={isVendor}
                onChange={(e) => setIsVendor(e.target.checked)}
                name="roleSwitch"
                inputProps={{ "aria-label": "role switch" }}
            />
            <div>
                <TextField
                    id="username"
                    label="Username"
                    variant="outlined"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </div>
            <div>
                <TextField
                    id="password"
                    label="Password"
                    variant="outlined"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>
            <Button type="submit" variant="contained" color="primary">
                Submit
            </Button>
            <div>
                <Button
                    onClick={handleBackToLogin}
                    variant="contained"
                    color="secondary"
                >
                    Back to Login
                </Button>
            </div>
        </form>
    );
};

export default Register;
