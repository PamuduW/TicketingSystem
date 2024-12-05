import React, { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { UserContext } from "../Common/UserContext";
import API from "../../axios";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";

const CreateEvent: React.FC = () => {
    const userContext = useContext(UserContext);
    const navigate = useNavigate();
    const [name, setName] = useState("");
    const [desc, setDesc] = useState("");
    const [totalTickets, setTotalTickets] = useState<number | "">("");
    const [maxCapacity, setMaxCapacity] = useState<number | "">("");

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (userContext?.userData) {
            const eventData = {
                name,
                ownerId: userContext.userData.userId,
                desc,
                totalTickets,
                maxCapacity,
            };

            try {
                const response = await API.post("/event", eventData, {
                    headers: {
                        "Content-Type": "application/json",
                    },
                });
                console.log("Event created:", response.data);
                navigate("/dashboard");
            } catch (error) {
                console.error("Error creating event:", error);
            }
        }
    };

    const handleBackToDashboard = () => {
        navigate("/dashboard");
    };

    return (
        <div>
            <div className={"buttons"} style={{ marginTop: 20 }}>
                <Button variant="text" onClick={handleBackToDashboard}>Back to Dashboard</Button>
            </div>
            <h1>Create Event</h1>
            <form onSubmit={handleSubmit}>
                <div className={"buttons"}>
                    <TextField
                        id="name"
                        label="Event Name"
                        required={true}
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                </div>
                <div className={"buttons"}>
                    <TextField
                        id="desc"
                        label="Event Description"
                        required={true}
                        type="text"
                        value={desc}
                        onChange={(e) => setDesc(e.target.value)}
                    />
                </div>
                <div className={"buttons"}>
                    <TextField
                        id="totalTickets"
                        label="Total Tickets"
                        required={true}
                        type="number"
                        value={totalTickets}
                        onChange={(e) =>
                            setTotalTickets(parseInt(e.target.value))
                        }
                    />
                </div>
                <div className={"buttons"}>
                    <TextField
                        id="maxCapacity"
                        label="Max Capacity"
                        required={true}
                        type="number"
                        value={maxCapacity}
                        onChange={(e) =>
                            setMaxCapacity(parseInt(e.target.value))
                        }
                    />
                </div>
                <div className={"buttons"}>
                    <Button variant="outlined" type="submit">Create Event</Button>
                </div>
            </form>
        </div>
    );
};

export default CreateEvent;