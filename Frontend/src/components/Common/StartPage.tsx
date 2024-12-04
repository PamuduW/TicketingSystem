// StartPage.tsx
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Register from "./Register";
import Login from "../Common/Login";
import Dashboard from "../Common/Dashboard";
import { UserProvider } from "./UserContext";
import Event from "./Event";
import Simulation from "../Simulation/Simulation.tsx";
import ChangeTickets from "./ChangeTickets.tsx";
import AddVendors from "../Vendor/AddVendors.tsx";
import CreateEvent from "../Vendor/CreateEvent.tsx";

function StartPage() {
    return (
        <UserProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/event/:eventId" element={<Event />} />
                    <Route
                        path="/simulation/:eventId"
                        element={<Simulation />}
                    />
                    <Route
                        path="/changeTickets/:eventId"
                        element={<ChangeTickets />}
                    />
                    <Route
                        path="/addVendors/:eventId"
                        element={<AddVendors />}
                    />
                    <Route path="/createEvent" element={<CreateEvent />} />
                </Routes>
            </Router>
        </UserProvider>
    );
}

export default StartPage;
