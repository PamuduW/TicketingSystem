import React, { useEffect, useState, useContext } from "react";
import API from "../../axios.tsx";
import { UserContext } from "./UserContext";

interface TicketData {
    [eventId: string]: string[];
}

/**
 * Component for displaying the customer's inventory of tickets.
 * Fetches ticket data and event names from the backend and displays them in a grid.
 */
const CustomerInventory: React.FC = () => {
    // Get user data from the UserContext
    const { userData } = useContext(UserContext) || {};
    // State to store ticket data
    const [ticketData, setTicketData] = useState<TicketData>({});
    // State to store event names
    const [eventNames, setEventNames] = useState<{ [key: string]: string }>({});

    /**
     * Fetches ticket data and event names when the component mounts or updates.
     */
    useEffect(() => {
        /**
         * Fetches ticket data for the current user.
         */
        const fetchTickets = async () => {
            if (userData) {
                try {
                    const { data } = await API.get(
                        `/event/tickets/${userData.userId}`
                    );
                    setTicketData(data);
                } catch (error) {
                    console.error("Error fetching tickets:", error);
                }
            }
        };

        /**
         * Fetches the name of an event given its ID.
         * @param eventId - The ID of the event to fetch.
         */
        const fetchEventName = async (eventId: string) => {
            try {
                const { data } = await API.get(`/event/${eventId}`);
                setEventNames((prev) => ({ ...prev, [eventId]: data.name }));
            } catch (error) {
                console.error(`Error fetching event ${eventId}:`, error);
            }
        };

        // Fetch tickets and then fetch event names for each ticket
        fetchTickets().then(() => {
            Object.keys(ticketData).forEach((eventId) => {
                if (!eventNames[eventId]) fetchEventName(eventId);
            });
        });
    }, [userData, ticketData, eventNames]);

    return (
        <div>
            <h2 style={{ margin: 50, textAlign: "center" }}>
                Customer Inventory
            </h2>
            {Object.entries(ticketData).map(([eventId, tickets]) => (
                <div key={eventId}>
                    <h3 className="user-details">
                        {eventNames[eventId] || "Loading..."}
                    </h3>
                    <div
                        style={{
                            display: "grid",
                            gridTemplateColumns: "repeat(4, 1fr)",
                            gap: "10px",
                            marginBottom: "20px",
                        }}
                    >
                        {tickets.map((ticket, index) => (
                            <div
                                key={index}
                                style={{
                                    border: "1px solid #ccc",
                                    padding: "10px",
                                    textAlign: "center",
                                }}
                            >
                                {ticket}
                            </div>
                        ))}
                    </div>
                </div>
            ))}
        </div>
    );
};

export default CustomerInventory;
