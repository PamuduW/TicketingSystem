import React, { useContext, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { UserContext } from "./UserContext";
import API from "../../axios";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import axios from "axios";

/**
 * Component for changing the number of tickets for an event.
 * Allows vendors to add tickets and customers to buy tickets.
 */
const ChangeTickets: React.FC = () => {
    // Get the eventId from the URL parameters
    const { eventId } = useParams<{ eventId: string }>();
    // Get the user data from the UserContext
    const { userData } = useContext(UserContext) || {};
    // Hook to navigate to different routes
    const navigate = useNavigate();
    // State to manage the ticket count input
    const [ticketCount, setTicketCount] = useState<number | "">("");
    // State to manage error messages
    const [errorMessage, setErrorMessage] = useState("");

    /**
     * Handle changes to the ticket count input field.
     * @param e - The change event from the input field.
     */
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTicketCount(parseInt(e.target.value, 10) || "");
    };

    /**
     * Handle form submission to update the ticket count.
     * @param e - The form submission event.
     */
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        // Validate that the ticket count is not negative
        if (typeof ticketCount === "number" && ticketCount < 0) {
            setErrorMessage("Cannot enter negative numbers");
            return;
        }
        // Proceed if userData is available and ticketCount is not empty
        if (userData && ticketCount !== "") {
            const { userId, isVendor } = userData;
            // Determine the URL based on whether the user is a vendor or customer
            const url = isVendor
                ? `/event/${eventId}/addTickets?ticketCount=${ticketCount}`
                : `/event/${eventId}/buyTickets?ticketCount=${ticketCount}&customerId=${userId}`;
            try {
                // Make the API request to update tickets
                await API.put(url, null, {
                    headers: { "Content-Type": "application/json" },
                });
                alert("Tickets updated successfully");
                navigate(`/event/${eventId}`);
            } catch (error: unknown) {
                // Handle errors from the API request
                if (axios.isAxiosError(error) && error.response) {
                    const { status, data } = error.response;
                    if (
                        status === 409 &&
                        data === "There are no tickets available"
                    ) {
                        setErrorMessage("Exceeded the available ticket limit");
                    } else if (status === 409) {
                        setErrorMessage("Exceeded the ticket pool limit");
                    } else if (status === 400) {
                        setErrorMessage("Exceeded the total ticket limit");
                    } else {
                        setErrorMessage("An error occurred. Please try again.");
                    }
                }
            }
        }
    };

    return (
        <form onSubmit={handleSubmit} style={{ textAlign: "center" }}>
            <h2 style={{ margin: 50 }}>
                {userData?.isVendor ? "Add Tickets" : "Buy Tickets"}
            </h2>
            <div style={{ paddingBottom: 15 }}>
                <TextField
                    required
                    label="Number of Tickets"
                    type="number"
                    value={ticketCount}
                    onChange={handleChange}
                />
            </div>
            {errorMessage && (
                <div style={{ marginBottom: 10, color: "red" }}>
                    {errorMessage}
                </div>
            )}
            <Button variant="outlined" type="submit">
                Submit
            </Button>
        </form>
    );
};

export default ChangeTickets;
