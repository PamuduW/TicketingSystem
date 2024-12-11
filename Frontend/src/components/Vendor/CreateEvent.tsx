import React, { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { UserContext } from "../Common/UserContext";
import API from "../../axios";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import axios from "axios";

/**
 * CreateEvent component for creating a new event.
 * Allows the user to input event details and submit them to the server.
 */
const CreateEvent: React.FC = () => {
    // Get the user data from the UserContext
    const { userData } = useContext(UserContext) || {};
    // Hook to navigate programmatically
    const navigate = useNavigate();
    // State to store the event name
    const [name, setName] = useState("");
    // State to store the event description
    const [desc, setDesc] = useState("");
    // State to store the total number of tickets
    const [totalTickets, setTotalTickets] = useState<number | "">("");
    // State to store the maximum capacity of the event
    const [maxCapacity, setMaxCapacity] = useState<number | "">("");
    // State to store the error message
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    /**
     * Handles the form submission to create a new event.
     * @param e - The form submission event.
     */
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
            } catch (error: unknown) {
                if (axios.isAxiosError(error) && error.response?.status === 409) {
                    setErrorMessage("Event with the same name already exists");
                } else {
                    console.error("Error creating event:", error);
                }
            }
        }
    };

    return (
        <div style={{ width: 400 }}>
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
                    onChange={(e) => {
                        const value = parseInt(e.target.value);
                        if (value > 0) {
                            setTotalTickets(value);
                            if (typeof maxCapacity === "number" && maxCapacity > value) {
                                setMaxCapacity(value);
                            }
                        }
                    }}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Max Capacity"
                    required
                    type="number"
                    value={maxCapacity}
                    onChange={(e) => {
                        const value = parseInt(e.target.value);
                        if (value > 0 && typeof totalTickets === "number" && value <= totalTickets) {
                            setMaxCapacity(value);
                        }
                    }}
                    fullWidth
                    margin="normal"
                />
                <div style={{ marginBottom: 20 }} />
                {errorMessage && (
                    <div style={{ marginBottom: 10, color: "red" }}>
                        {errorMessage}
                    </div>
                )}
                <Button variant="outlined" type="submit" fullWidth>
                    Create Event
                </Button>
            </form>
        </div>
    );
};

export default CreateEvent;