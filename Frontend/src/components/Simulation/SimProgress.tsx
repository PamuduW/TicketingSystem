import React, { useEffect, useState, useContext } from "react";
import { UserContext } from "../Common/UserContext";
import { LineChart } from "@mui/x-charts/LineChart";

const SimProgress: React.FC = () => {
    const { userData } = useContext(UserContext) || {};
    const [currentTickets, setCurrentTickets] = useState<number[]>([]);
    const [allSoldTickets, setAllSoldTickets] = useState<number[]>([]);
    const [xLabels, setXLabels] = useState<string[]>([]);

    useEffect(() => {
        if (userData) {
            // const socket = new WebSocket(`ws://localhost:8080/ws/integers`);
            const socket = new WebSocket(
                `wss://ticketing---system-32a1f2f59169.herokuapp.com/ws/integers`
            );

            socket.onopen = () =>
                console.log("WebSocket connection established");

            socket.onmessage = (event) => {
                const parts = event.data.split(",").map(Number);
                const half = Math.ceil(parts.length / 2);
                setCurrentTickets((prev) => [...prev, ...parts.slice(0, half)]);
                setAllSoldTickets((prev) => [...prev, ...parts.slice(half)]);
                setXLabels((prev) => [...prev, `Update ${prev.length + 1}`]);
            };

            socket.onerror = (error) =>
                console.error("WebSocket error:", error);
            socket.onclose = () => console.log("WebSocket connection closed");

            return () => socket.close();
        }
    }, [userData]);

    const allAddedTickets = currentTickets.map(
        (ticket, index) => ticket + (allSoldTickets[index] || 0)
    );

    return (
        <div>
            <LineChart
                width={1000}
                height={500}
                series={[
                    { data: allSoldTickets, label: "All Sold Tickets" },
                    { data: currentTickets, label: "Current Tickets" },
                    { data: allAddedTickets, label: "All Added Tickets" },
                ]}
                xAxis={[{ scaleType: "point", data: xLabels }]}
            />
        </div>
    );
};

export default SimProgress;
