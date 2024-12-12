import React, { useContext, useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { UserContext } from "./UserContext";
import API from "../../axios";
import { Button, Drawer } from "@mui/material";
import ChangeTickets from "./ChangeTickets";
import AddVendors from "../Vendor/AddVendors";
import UpdateEvent from "../Vendor/UpdateEvent";

interface Event {
    eventId: string;
    name: string;
    ownerId: string;
    desc: string;
    totalTickets: number;
    maxCapacity: number;
    currentTickets: number;
    issuedTickets: number;
    totalTicketsAdded: number;
    vendors: string[];
}

interface Vendor {
    userId: string;
    username: string;
}

/**
 * Component for displaying event details and providing options for vendors and customers.
 * Fetches event and vendor details from the backend and displays them.
 */
const Event: React.FC = () => {
    // Get the eventId from the URL parameters
    const { eventId } = useParams<{ eventId: string }>();
    // Get the user data from the UserContext
    const { userData } = useContext(UserContext) || {};
    // Hook to navigate to different routes
    const navigate = useNavigate();
    // State to store the event details
    const [event, setEvent] = useState<Event | null>(null);
    // State to store vendor details
    const [vendorDetails, setVendorDetails] = useState<{
        [key: string]: Vendor;
    }>({});
    // State to manage the drawer open/close status for changing tickets
    const [drawerOpen, setDrawerOpen] = useState(false);
    // State to manage the drawer open/close status for adding vendors
    const [vendorDrawerOpen, setVendorDrawerOpen] = useState(false);
    // State to manage the drawer open/close status for updating the event
    const [updateDrawerOpen, setUpdateDrawerOpen] = useState(false);

    /**
     * Fetches vendor details for a given vendor ID.
     * @param vendorId - The ID of the vendor to fetch.
     */
    const fetchVendorDetails = async (vendorId: string) => {
        try {
            const { data } = await API.get(`/vendor/${vendorId}`, {
                headers: { "Content-Type": "application/json" },
            });
            setVendorDetails((prev) => ({ ...prev, [vendorId]: data }));
        } catch (error) {
            console.error(`Error fetching vendor ${vendorId}:`, error);
        }
    };

    /**
     * Fetches event details for the current event ID.
     */
    const fetchEvent = async () => {
        if (userData) {
            try {
                const { data } = await API.get(`/event/${eventId}`, {
                    headers: { "Content-Type": "application/json" },
                });
                setEvent(data);
            } catch (error) {
                console.error("Error fetching event:", error);
            }
        }
    };

    // Fetch event details when the component mounts or updates
    useEffect(() => {
        fetchEvent();
    }, [eventId, userData]);

    // Fetch vendor details for each vendor in the event
    useEffect(() => {
        event?.vendors.forEach((vendorId) => {
            if (!vendorDetails[vendorId]) fetchVendorDetails(vendorId);
        });
    }, [event, vendorDetails]);

    // Fetch event details when any drawer is closed
    useEffect(() => {
        if (!drawerOpen || !vendorDrawerOpen || !updateDrawerOpen) fetchEvent();
    }, [drawerOpen, vendorDrawerOpen, updateDrawerOpen]);

    if (!event) return <p>Loading...</p>;

    /**
     * Handles the click to navigate to the event simulation page.
     */
    const handleSimulateEvent = () => navigate(`/simulation/${eventId}`);

    /**
     * Opens the drawer to change tickets.
     */
    const handleChangeTickets = () => setDrawerOpen(true);

    /**
     * Opens the drawer to add vendors.
     */
    const handleAddVendors = () => setVendorDrawerOpen(true);

    /**
     * Opens the drawer to update the event.
     */
    const handleUpdateEvent = () => setUpdateDrawerOpen(true);

    /**
     * Navigates back to the dashboard.
     */
    const handleBackToDashboard = () => navigate("/dashboard");

    return (
        <div>
            <div className="user-details">
                <Button onClick={handleBackToDashboard}>
                    Back to Dashboard
                </Button>
            </div>
            <h1>{event.name}</h1>
            <h3 className={"user-details"}>Event Description</h3>
            <p className={"user-details"} style={{ marginBottom: 30 }}>
                {event.desc}
            </p>
            {userData?.isVendor && (
                <p className="user-details">
                    Owner:{" "}
                    {vendorDetails[event.ownerId]?.username || "Loading..."}
                </p>
            )}
            <p className="user-details">Total Tickets: {event.totalTickets}</p>
            <p className="user-details">Max Capacity: {event.maxCapacity}</p>
            <p className="user-details">
                Current Tickets: {event.currentTickets}
            </p>
            <p className="user-details">
                Issued Tickets: {event.issuedTickets}
            </p>
            <p className="user-details">
                Total Tickets Added: {event.totalTicketsAdded}
            </p>

            {userData?.userId === event.ownerId && (
                <>
                    <h3 className="user-details" style={{ paddingTop: 16 }}>
                        Vendors
                    </h3>
                    {event.vendors.map((vendorId) => (
                        <p
                            className="user-details"
                            style={{ margin: 1 }}
                            key={vendorId}
                        >
                            {vendorDetails[vendorId]?.username || "Loading..."}
                        </p>
                    ))}
                </>
            )}
            <div style={{ marginTop: 15 }}>
                {userData?.isVendor ? (
                    <>
                        ---------------------------------------------------------------------------
                        <h3 style={{ margin: 1 }}>Vendor Options</h3>
                        <div className="buttons">
                            <Button
                                variant="outlined"
                                onClick={handleChangeTickets}
                            >
                                Add Tickets
                            </Button>
                            <Button
                                variant="outlined"
                                onClick={handleSimulateEvent}
                            >
                                Simulate Event
                            </Button>
                        </div>
                        <div style={{ marginTop: 15 }}>
                            {userData.userId === event.ownerId && (
                                <>
                                    ---------------------------------------------------------------------------
                                    <h3 style={{ margin: 1 }}>Owner Options</h3>
                                    <div className="buttons">
                                        <Button
                                            variant="outlined"
                                            onClick={handleAddVendors}
                                        >
                                            Change Vendors
                                        </Button>
                                        <Button
                                            variant="outlined"
                                            onClick={handleUpdateEvent}
                                        >
                                            Update Event
                                        </Button>
                                    </div>
                                </>
                            )}
                        </div>
                    </>
                ) : (
                    <div style={{ marginTop: 10 }}>
                        <h3 style={{ margin: 1 }}>Customer Options</h3>
                        <div className="buttons">
                            <Button
                                variant="outlined"
                                onClick={handleChangeTickets}
                            >
                                Buy Tickets
                            </Button>
                        </div>
                    </div>
                )}
            </div>
            <Drawer
                anchor="right"
                open={drawerOpen}
                onClose={() => setDrawerOpen(false)}
                sx={{ "& .MuiDrawer-paper": { backgroundColor: "#d4d4d4" } }}
            >
                <div style={{ width: 300, padding: 20 }}>
                    <ChangeTickets />
                </div>
            </Drawer>
            <Drawer
                anchor="right"
                open={vendorDrawerOpen}
                onClose={() => setVendorDrawerOpen(false)}
                sx={{ "& .MuiDrawer-paper": { backgroundColor: "#d4d4d4" } }}
            >
                <div style={{ width: 300, padding: 20 }}>
                    <AddVendors />
                </div>
            </Drawer>
            <Drawer
                anchor="right"
                open={updateDrawerOpen}
                onClose={() => setUpdateDrawerOpen(false)}
                sx={{ "& .MuiDrawer-paper": { backgroundColor: "#d4d4d4" } }}
            >
                <div style={{ width: 300, padding: 20 }}>
                    <UpdateEvent />
                </div>
            </Drawer>
        </div>
    );
};

export default Event;
