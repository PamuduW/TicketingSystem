import React, { useContext, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { UserContext } from "./UserContext";
import API from "../../axios";

const ChangeTickets: React.FC = () => {
    const { eventId } = useParams<{ eventId: string }>();
    const userContext = useContext(UserContext);
    const navigate = useNavigate();
    const [ticketCount, setTicketCount] = useState<number>(0);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTicketCount(parseInt(e.target.value, 10));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (userContext?.userData) {
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
            } catch (error) {
                console.error("Error:", error);
            }
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <label>
                Number of Tickets:
                <input
                    type="number"
                    value={ticketCount}
                    onChange={handleChange}
                />
            </label>
            <button type="submit">Submit</button>
        </form>
    );
};

export default ChangeTickets;
