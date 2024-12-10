import React, { useContext, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { UserContext } from "./UserContext";
import API from "../../axios";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import axios from "axios";

const ChangeTickets: React.FC = () => {
    const { eventId } = useParams<{ eventId: string }>();
    const { userData } = useContext(UserContext) || {};
    const navigate = useNavigate();
    const [ticketCount, setTicketCount] = useState<number | "">("");
    const [errorMessage, setErrorMessage] = useState("");

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTicketCount(parseInt(e.target.value, 10) || "");
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (typeof ticketCount === "number" && ticketCount < 0) {
            setErrorMessage("Cannot enter negative numbers");
            return;
        }
        if (userData && ticketCount !== "") {
            const { userId, isVendor } = userData;
            const url = isVendor
                ? `/event/${eventId}/addTickets?ticketCount=${ticketCount}`
                : `/event/${eventId}/buyTickets?ticketCount=${ticketCount}&customerId=${userId}`;
            try {
                await API.put(url, null, {
                    headers: { "Content-Type": "application/json" },
                });
                alert("Tickets updated successfully");
                navigate(`/event/${eventId}`);
            } catch (error: unknown) {
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
