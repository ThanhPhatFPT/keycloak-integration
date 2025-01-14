import React, { useEffect, useState } from "react";
import axios from "axios";

function Dashboard() {
    const [profiles, setProfiles] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchProfiles = async () => {
            setLoading(true);
            setError(null);

            // Lấy token từ localStorage
            const token = localStorage.getItem("token");

            if (!token) {
                setError("Unauthorized: No token found");
                setLoading(false);
                return;
            }

            try {
                // Gọi API với token trong Authorization header
                const response = await axios.get("http://localhost:8080/profile/profiles", {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                // Cập nhật danh sách hồ sơ vào state
                setProfiles(response.data.result);
            } catch (err) {
                console.error(err);
                setError(
                    err.response?.data?.message || "An error occurred while fetching profiles."
                );
            } finally {
                setLoading(false);
            }
        };

        fetchProfiles();
    }, []);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div className="text-red-500">{error}</div>;
    }

    return (
        <div className="min-h-screen bg-gray-100 p-6">
            <h1 className="text-3xl font-bold mb-4">Dashboard</h1>
            <div className="bg-white p-4 rounded shadow-md">
                <h2 className="text-2xl font-semibold mb-4">User Profiles</h2>
                {profiles.length === 0 ? (
                    <p>No profiles found.</p>
                ) : (
                    <table className="w-full table-auto border-collapse border border-gray-300">
                        <thead>
                        <tr className="bg-gray-200 text-left">
                            <th className="border border-gray-300 px-4 py-2">Username</th>
                            <th className="border border-gray-300 px-4 py-2">Email</th>
                            <th className="border border-gray-300 px-4 py-2">First Name</th>
                            <th className="border border-gray-300 px-4 py-2">Last Name</th>
                            <th className="border border-gray-300 px-4 py-2">Date of Birth</th>
                        </tr>
                        </thead>
                        <tbody>
                        {profiles.map((profile) => (
                            <tr key={profile.id}>
                                <td className="border border-gray-300 px-4 py-2">{profile.username}</td>
                                <td className="border border-gray-300 px-4 py-2">{profile.email}</td>
                                <td className="border border-gray-300 px-4 py-2">{profile.firstName}</td>
                                <td className="border border-gray-300 px-4 py-2">{profile.lastName}</td>
                                <td className="border border-gray-300 px-4 py-2">{profile.dob}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}

export default Dashboard;
