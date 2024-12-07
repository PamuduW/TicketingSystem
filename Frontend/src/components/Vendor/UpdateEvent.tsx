import React, { useContext, useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { UserContext } from "../Common/UserContext";
import API from "../../axios";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";

interface Event {
    eventId: string;
    name: string;
    desc: string;
    totalTickets: number;
    maxCapacity: number;
    issuedTickets: number;
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
    const [error, setError] = useState<string | null>(null);

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
        if (formData.totalTickets < 0 || formData.maxCapacity < 0) {
            setError("Cannot enter negative numbers");
            return;
        }
        if (formData.totalTickets < event!.issuedTickets) {
            setError(
                `Total tickets cannot be less than issued tickets (${event!.issuedTickets})`
            );
            return;
        }
        if (formData.maxCapacity > formData.totalTickets) {
            setError("Max capacity cannot be greater than total tickets");
            return;
        }
        setError(null);
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
        <div style={{ textAlign: "center" }}>
            <h2 style={{ margin: 50 }}>Update Event</h2>
            <form onSubmit={handleSubmit}>
                {error && (
                    <p style={{ marginBottom: 10, color: "red" }}>{error}</p>
                )}
                <div style={{ paddingBottom: 20 }}>
                    <TextField
                        required={true}
                        label={"Name"}
                        type="text"
                        id="name"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                    />
                </div>
                <div style={{ paddingBottom: 20 }}>
                    <TextField
                        required={true}
                        label={"Description"}
                        type="text"
                        id="desc"
                        name="desc"
                        value={formData.desc}
                        onChange={handleChange}
                    />
                </div>
                <div style={{ paddingBottom: 20 }}>
                    <TextField
                        required={true}
                        label={"Total Tickets"}
                        type="number"
                        id="totalTickets"
                        name="totalTickets"
                        value={formData.totalTickets}
                        onChange={handleChange}
                    />
                </div>
                <div style={{ paddingBottom: 20 }}>
                    <TextField
                        required={true}
                        label={"Max Capacity"}
                        type="number"
                        id="maxCapacity"
                        name="maxCapacity"
                        value={formData.maxCapacity}
                        onChange={handleChange}
                    />
                </div>
                <Button variant="outlined" type="submit">
                    Update Event
                </Button>
            </form>
        </div>
    );
};

export default UpdateEvent;
