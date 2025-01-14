import React, { useEffect, useState } from "react";
import {jwtDecode} from "jwt-decode"; // Correct import for jwt-decode

function Profile() {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isAdmin, setIsAdmin] = useState(false); // State để lưu thông tin admin role

    useEffect(() => {
        // Lấy token từ localStorage
        const token = localStorage.getItem("token");

        if (!token) {
            setError("You must be logged in to view this page.");
            setLoading(false);
            return;
        }

        try {
            // Giải mã token để lấy thông tin profile
            const decodedToken = jwtDecode(token);
            console.log("Decoded token:", decodedToken); // Log toàn bộ thông tin của token

            // Kiểm tra role admin trong realm_access.roles
            const roles = decodedToken.realm_access?.roles || [];
            const hasAdminRole = roles.includes("admin");
            setIsAdmin(hasAdminRole);

            // Extract profile information from the token
            const profileData = {
                username: decodedToken.preferred_username || decodedToken.username || "Unknown",
                email: decodedToken.email || "Not provided",
                firstName: decodedToken.given_name || "Not provided",
                lastName: decodedToken.family_name || "Not provided",
                dob: decodedToken.dob || "Not provided", // If `dob` is included in the token
            };

            setProfile(profileData); // Cập nhật thông tin profile
        } catch (err) {
            console.error(err);
            setError("Invalid token or token expired.");
        } finally {
            setLoading(false);
        }
    }, []);

    if (loading) {
        return <p>Loading...</p>;
    }

    if (error) {
        return <p className="text-red-500">{error}</p>;
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="w-full max-w-md p-8 bg-white rounded shadow-md">
                <h2 className="text-2xl font-bold text-center mb-6">Profile Information</h2>
                {profile && (
                    <div>
                        <p>
                            <strong>Username:</strong> {profile.username}
                        </p>
                        <p>
                            <strong>Email:</strong> {profile.email}
                        </p>
                        <p>
                            <strong>First Name:</strong> {profile.firstName}
                        </p>
                        <p>
                            <strong>Last Name:</strong> {profile.lastName}
                        </p>
                        <p>
                            <strong>Date of Birth:</strong> {profile.dob}
                        </p>
                        <p>
                            <strong>Admin Access:</strong> {isAdmin ? "Yes" : "No"}
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Profile;
