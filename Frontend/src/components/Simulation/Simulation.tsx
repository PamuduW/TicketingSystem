import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import SimInputForm from "./SimInputForm.tsx";
import SimLog from "./SimLog.tsx";
import SimProgress from "./SimProgress.tsx";
import SimData from "./SimData.tsx";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";

const Simulation: React.FC = () => {
    const { eventId } = useParams<{ eventId: string }>();
    const navigate = useNavigate();

    if (!eventId) {
        return <p>Error: Event ID is missing</p>;
    }

    const handleBackToEvent = () => {
        navigate(`/event/${eventId}`);
    };

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
                    gap: "20px",
                }}
            >
                <SimInputForm eventId={eventId} />
                <SimData eventId={eventId} />
            </div>
            <SimProgress />
            <Box sx={{ height: 300, overflowY: "auto", marginTop: 2 }}>
                <SimLog />
            </Box>
        </>
    );
};

export default Simulation;
