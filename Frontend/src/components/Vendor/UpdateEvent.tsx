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

/**
 * UpdateEvent component for updating the details of an existing event.
 * Fetches the current event details and allows the user to update them.
 */
const UpdateEvent: React.FC = () => {
    // Extract the eventId parameter from the URL
    const { eventId } = useParams<{ eventId: string }>();
    // Get the user data from the UserContext
    const { userData } = useContext(UserContext) || {};
    // Hook to navigate programmatically
    const navigate = useNavigate();
    // State to store the event details
    const [event, setEvent] = useState<Event | null>(null);
    // State to store the form data for updating the event
    const [formData, setFormData] = useState({
        name: "",
        desc: "",
        totalTickets: 0,
        maxCapacity: 0,
    });
    // State to store any error messages
    const [error, setError] = useState<string | null>(null);

    /**
     * Fetches the event details for the current event ID.
     */
    useEffect(() => {
        const fetchEvent = async () => {
            if (userData) {
                try {
                    const response = await API.get(`/event/${eventId}`);
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
    }, [eventId, userData]);

    /**
     * Handles changes to the form input fields.
     * @param e - The change event for the input field.
     */
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    /**
     * Handles the form submission to update the event details.
     * @param e - The form submission event.
     */
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
            await API.put(`/event/${eventId}`, formData);
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
                        required
                        label="Name"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                    />
                </div>
                <div style={{ paddingBottom: 20 }}>
                    <TextField
                        required
                        label="Description"
                        name="desc"
                        value={formData.desc}
                        onChange={handleChange}
                    />
                </div>
                <div style={{ paddingBottom: 20 }}>
                    <TextField
                        required
                        label="Total Tickets"
                        type="number"
                        name="totalTickets"
                        value={formData.totalTickets}
                        onChange={handleChange}
                    />
                </div>
                <div style={{ paddingBottom: 20 }}>
                    <TextField
                        required
                        label="Max Capacity"
                        type="number"
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