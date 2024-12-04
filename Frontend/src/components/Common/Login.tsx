import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import API from "../../axios";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Switch from "@mui/material/Switch";
import { UserContext } from "./UserContext";

const Login: React.FC = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isVendor, setIsVendor] = useState(true);
    const navigate = useNavigate();
    const userContext = useContext(UserContext);

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        const params = {
            username: username,
            pass: password,
        };
        const url = isVendor ? "/vendor" : "/customer";
        console.log("Sending params:", params);
        try {
            const response = await API.get(url, {
                params: params,
                headers: {
                    "Content-Type": "application/json",
                },
            });
            console.log("Response:", response.data);
            if (userContext) {
                userContext.setUserData({
                    username: response.data.username,
                    userId: isVendor
                        ? response.data.vendorId
                        : response.data.customerId, // Save vendorId or customerId based on switch state
                    isVendor: isVendor, // Save isVendor as true if the switch is on
                });
            }
            navigate("/dashboard"); // Redirect to dashboard page on success
        } catch (error) {
            console.error("Error:", error);
        }
    };

    const handleCreateProfile = () => {
        navigate("/register");
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
                    onClick={handleCreateProfile}
                    variant="contained"
                    color="primary"
                >
                    Create Account
                </Button>
            </div>
        </form>
    );
};

export default Login;
