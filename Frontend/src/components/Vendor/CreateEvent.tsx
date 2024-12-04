import React, { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { UserContext } from "../Common/UserContext";
import API from "../../axios";

const CreateEvent: React.FC = () => {
    const userContext = useContext(UserContext);
    const navigate = useNavigate();
    const [name, setName] = useState("");
    const [desc, setDesc] = useState("");
    const [totalTickets, setTotalTickets] = useState(0);
    const [maxCapacity, setMaxCapacity] = useState(0);

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

    return (
        <div>
            <h1>Create Event</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Event Name:</label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Event Description:</label>
                    <input
                        type="text"
                        value={desc}
                        onChange={(e) => setDesc(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Total Tickets:</label>
                    <input
                        type="number"
                        value={totalTickets}
                        onChange={(e) =>
                            setTotalTickets(parseInt(e.target.value))
                        }
                        required
                    />
                </div>
                <div>
                    <label>Max Capacity:</label>
                    <input
                        type="number"
                        value={maxCapacity}
                        onChange={(e) =>
                            setMaxCapacity(parseInt(e.target.value))
                        }
                        required
                    />
                </div>
                <button type="submit">Create Event</button>
            </form>
        </div>
    );
};

export default CreateEvent;
