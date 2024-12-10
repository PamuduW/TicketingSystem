import React, { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { UserContext } from "../Common/UserContext";
import API from "../../axios";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";

const CreateEvent: React.FC = () => {
    const { userData } = useContext(UserContext) || {};
    const navigate = useNavigate();
    const [name, setName] = useState("");
    const [desc, setDesc] = useState("");
    const [totalTickets, setTotalTickets] = useState<number | "">("");
    const [maxCapacity, setMaxCapacity] = useState<number | "">("");

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (userData) {
            const eventData = {
                name,
                ownerId: userData.userId,
                desc,
                totalTickets,
                maxCapacity,
            };

            try {
                await API.post("/event", eventData, {
                    headers: { "Content-Type": "application/json" },
                });
                navigate("/dashboard");
            } catch (error) {
                console.error("Error creating event:", error);
            }
        }
    };

    return (
        <div>
            <Button
                variant="text"
                onClick={() => navigate("/dashboard")}
                style={{ marginTop: 20 }}
            >
                Back to Dashboard
            </Button>
            <h1>Create Event</h1>
            <form onSubmit={handleSubmit}>
                <TextField
                    label="Event Name"
                    required
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Event Description"
                    required
                    value={desc}
                    onChange={(e) => setDesc(e.target.value)}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Total Tickets"
                    required
                    type="number"
                    value={totalTickets}
                    onChange={(e) => setTotalTickets(parseInt(e.target.value))}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Max Capacity"
                    required
                    type="number"
                    value={maxCapacity}
                    onChange={(e) => setMaxCapacity(parseInt(e.target.value))}
                    fullWidth
                    margin="normal"
                />
                <Button variant="outlined" type="submit" fullWidth>
                    Create Event
                </Button>
            </form>
        </div>
    );
};

export default CreateEvent;
