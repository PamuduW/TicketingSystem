import React, { useContext, useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { UserContext } from "../Common/UserContext";
import API from "../../axios";

interface Event {
    eventId: string;
    name: string;
    desc: string;
    totalTickets: number;
    maxCapacity: number;
}

const UpdateEvent: React.FC = () => {
    const { eventId } = useParams<{ eventId: string }>();
    const userContext = useContext(UserContext);
    const navigate = useNavigate();
    const [event, setEvent] = useState<Event | null>(null);
    const [formData, setFormData] = useState({
        name: "",
        desc: "",
        totalTickets: 0,
        maxCapacity: 0,
    });

    useEffect(() => {
        const fetchEvent = async () => {
            if (userContext?.userData) {
                const url = `/event/${eventId}`;
                try {
                    const response = await API.get(url, {
                        headers: {
                            "Content-Type": "application/json",
                        },
                    });
                    setEvent(response.data);
                    setFormData({
                        name: response.data.name,
                        desc: response.data.desc,
                        totalTickets: response.data.totalTickets,
                        maxCapacity: response.data.maxCapacity,
                    });
                } catch (error) {
                    console.error("Error fetching event:", error);
                }
            }
        };

        fetchEvent();
    }, [eventId, userContext]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await API.put(`/event/${eventId}`, formData, {
                headers: {
                    "Content-Type": "application/json",
                },
            });
            navigate(`/event/${eventId}`);
        } catch (error) {
            console.error("Error updating event:", error);
        }
    };

    if (!event) {
        return <p>Loading...</p>;
    }

    return (
        <div>
            <h1>Update Event</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="name">Name:</label>
                    <input
                        type="text"
                        id="name"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                    />
                </div>
                <div>
                    <label htmlFor="desc">Description:</label>
                    <input
                        type="text"
                        id="desc"
                        name="desc"
                        value={formData.desc}
                        onChange={handleChange}
                    />
                </div>
                <div>
                    <label htmlFor="totalTickets">Total Tickets:</label>
                    <input
                        type="number"
                        id="totalTickets"
                        name="totalTickets"
                        value={formData.totalTickets}
                        onChange={handleChange}
                    />
                </div>
                <div>
                    <label htmlFor="maxCapacity">Max Capacity:</label>
                    <input
                        type="number"
                        id="maxCapacity"
                        name="maxCapacity"
                        value={formData.maxCapacity}
                        onChange={handleChange}
                    />
                </div>
                <button type="submit">Update Event</button>
            </form>
        </div>
    );
};

export default UpdateEvent;