import React, { useEffect, useState, useContext } from "react";
import { UserContext } from "../Common/UserContext";
import API from "../../axios.tsx";

interface SimDataProps {
    eventId: string;
}

interface Event {
    eventId: string;
    name: string;
    totalTickets: number;
    maxCapacity: number;
}

const SimData: React.FC<SimDataProps> = ({ eventId }) => {
    const userContext = useContext(UserContext);
    const [event, setEvent] = useState<Event | null>(null);
    const [currentTickets, setCurrentTickets] = useState<number[]>([]);
    const [allSoldTickets, setAllSoldTickets] = useState<number[]>([]);

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
            } catch (error) {
                console.error("Error fetching event:", error);
            }
        }
    };

    useEffect(() => {
        fetchEvent();
    }, [eventId, userContext]);

    useEffect(() => {
        if (userContext?.userData) {
            // const socket = new WebSocket(`ws://localhost:8080/ws/integers`);
            const socket = new WebSocket(
                `wss://ticketing---system-32a1f2f59169.herokuapp.com/ws/integers`
            );

            socket.onopen = () => {
                console.log("WebSocket connection established");
            };

            socket.onmessage = (event) => {
                const message = event.data;
                const parts = message.split(",").map(Number);
                const half = Math.ceil(parts.length / 2);
                const newCurrentTickets = parts.slice(0, half);
                const newAllSoldTickets = parts.slice(half);

                setCurrentTickets(newCurrentTickets);
                setAllSoldTickets(newAllSoldTickets);
            };

            socket.onerror = (error) => {
                console.error("WebSocket error:", error);
            };

            socket.onclose = () => {
                console.log("WebSocket connection closed");
            };

            return () => {
                socket.close();
            };
        }
    }, [userContext]);

    if (!event) {
        return <p>Loading...</p>;
    }

    const totalTickets = currentTickets.reduce((acc, ticket) => acc + ticket, 0) +
        allSoldTickets.reduce((acc, ticket) => acc + ticket, 0);


    return (
        <div>
            {event.name}
            ----------------
            {event.totalTickets}
            ----------------
            {event.maxCapacity}
            ----------------
            currentTickets: {currentTickets}
            ----------------
            allSoldTickets: {allSoldTickets}
            ----------------
            allAddedTickets: {totalTickets}


        </div>
    );
};

export default SimData;
