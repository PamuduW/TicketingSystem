import React, { useContext, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { UserContext } from "./UserContext";
import API from "../../axios";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import axios from "axios";

const ChangeTickets: React.FC = () => {
    const { eventId } = useParams<{ eventId: string }>();
    const userContext = useContext(UserContext);
    const navigate = useNavigate();
    const [ticketCount, setTicketCount] = useState<number | "">("");
    const [errorMessage, setErrorMessage] = useState("");


    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTicketCount(parseInt(e.target.value, 10) || "");
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (userContext?.userData && ticketCount !== "") {
            const userId = userContext.userData.userId;
            const isVendor = userContext.userData.isVendor;
            const url = isVendor
                ? `/event/${eventId}/addTickets?ticketCount=${ticketCount}`
                : `/event/${eventId}/buyTickets?ticketCount=${ticketCount}&customerId=${userId}`;
            try {
                const response = await API.put(url, null, {
                    headers: {
                        "Content-Type": "application/json",
                    },
                });
                console.log("Response:", response.data);
                navigate(`/event/${eventId}`);
            } catch (error : unknown) {
                console.error("Error:", error);
                if (axios.isAxiosError(error) && error.response) {
                    if (error.response.status === 409) {
                        setErrorMessage("Exceeded the ticket pool limit");
                    }else if (error.response.status === 400) {
                        setErrorMessage("Exceeded the total ticket limit");
                    } else {
                        setErrorMessage("An error occurred. Please try again.");
                    }}
            }
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <div style={{ paddingBottom: 15 }}>
                <TextField
                    label={"Number of Tickets"}
                    type="number"
                    value={ticketCount}
                    onChange={handleChange}
                />
            </div>
            {errorMessage && (
                <div style={{ paddingBottom: 10, color: "red" }}>
                    {errorMessage}
                </div>
            )}
            <Button variant="outlined" type="submit">Submit</Button>
        </form>
    );
};

export default ChangeTickets;