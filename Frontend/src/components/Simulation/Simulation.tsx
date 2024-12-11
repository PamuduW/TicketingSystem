import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import SimInputForm from "./SimInputForm.tsx";
import SimLog from "./SimLog.tsx";
import SimProgress from "./SimProgress.tsx";
import SimData from "./SimData.tsx";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";

/**
 * Simulation component for managing and displaying the simulation of an event.
 * Includes input form, data display, progress chart, and log messages.
 */
const Simulation: React.FC = () => {
    // Extract the eventId parameter from the URL
    const { eventId } = useParams<{ eventId: string }>();
    // Hook to navigate programmatically
    const navigate = useNavigate();
    // State to trigger reload of child components
    const [reload, setReload] = useState(false);

    // Display error message if eventId is missing
    if (!eventId) return <p>Error: Event ID is missing</p>;

    /**
     * Handles navigation back to the event page.
     */
    const handleBackToEvent = () => navigate(`/event/${eventId}`);

    /**
     * Toggles the reload state to trigger child component updates.
     */
    const handleReload = () => setReload(!reload);

    return (
        <>
            <Button variant="text" onClick={handleBackToEvent}>
                Back to Event
            </Button>
            <h1>Simulation</h1>
            <div style={{ margin: 50 }}></div>
            <div
                style={{
                    display: "grid",
                    gridTemplateColumns: "1fr 1fr",
                    gap: 20,
                    marginBottom: 50,
                }}
            >
                <SimInputForm eventId={eventId} onReload={handleReload} />
                <SimData eventId={eventId} />
            </div>
            <SimProgress key={String(reload)} />
            <Box sx={{ height: 300, overflowY: "auto", marginTop: 2 }}>
                <SimLog key={String(reload)} />
            </Box>
        </>
    );
};

export default Simulation;