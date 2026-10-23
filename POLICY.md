# Privacy Policy for CodeEditor

**Last updated:** October 23, 2026

Welcome to **CodeEditor** ("we", "our", or "us"). We are committed to protecting your privacy. This Privacy Policy explains our practices regarding data collection, usage, and disclosure when you use the CodeEditor mobile application for Android.

---

## 1. Summary: Privacy by Design

CodeEditor is designed primarily as an offline-first, local text and source code editor. **We do not collect, transmit, store, sell, or share your personal documents or file contents.**

- **No File Tracking:** All file viewing, syntax highlighting, encoding conversions, search/replace, and text edits occur locally on your device.
- **Controlled File Access:** File read/write access is mediated strictly through Android's Storage Access Framework (SAF) based on your explicit selections.
- **No Direct Personal Data Collection:** We do not require registration, accounts, or collection of personal identifying details (such as your name, email, or physical address).

---

## 2. Information We Handle

### A. File Contents and Code
When you open, edit, or create files within CodeEditor, those files remain entirely under your control on your local device or your designated storage provider. We do not transmit or upload your code, text, or file contents to any external servers.

### B. Personal Information
We do not collect personal identifying information (e.g., name, phone number, contacts).

### C. Device & Technical Information
The application itself does not log or collect personal telemetry. However, if third-party services (such as advertising networks) are integrated, certain technical data may be processed as described below.

---

## 3. Advertising & Third-Party Services (Future / Optional)

Currently, the application does not serve advertisements. In future versions, we may introduce advertisements to support ongoing development:

- **Ad Networks:** If advertising is enabled in future releases, we may partner with third-party ad providers (such as Google AdMob).
- **Data Processed by Ad Providers:** Third-party ad networks may automatically collect and process certain non-sensitive device data, including:
  - Advertising identifiers (e.g., Google Advertising ID / GAID)
  - Device model, operating system version, and general network information
  - Non-precise location data (e.g., country or region derived from IP address)
  - Ad interaction and performance metrics (impressions, clicks)
- **User Control & Opt-Out:** You can reset or limit personalized advertising at any time through your Android device settings (`Settings` > `Google` > `Ads` or `Privacy`).
- **File Privacy Unaffected:** Advertising partners never receive access to your files, text, or source code.

---

## 4. Device Permissions & File Access

CodeEditor is built in accordance with modern Android security standards and utilizes the **Storage Access Framework (SAF)**:

- **Scoped Storage & SAF Picker:** The application does not request broad file access permissions (such as `READ_EXTERNAL_STORAGE` or `MANAGE_EXTERNAL_STORAGE`).
- **Explicit User Selection:** File access is granted only when you explicitly select files or folders using the system document picker (`OpenMultipleDocuments`, `CreateDocument`).
- **Transient and Directed Access:** Access to file URIs is restricted to opening, reading, and saving the files you have explicitly chosen to edit.
- **Network Permissions:** If advertising features are deployed in a future release, the app may request Android's standard network permission (`android.permission.INTERNET`) solely to load ad inventory.

---

## 5. Data Retention & Deletion

Because CodeEditor does not store your files or personal documents on external servers:
- We retain no personal user files or database records.
- If you uninstall the application from your device, any temporary local application cache maintained by Android is removed. Your saved files stored on your device or cloud storage remain untouched in their original locations.

---

## 6. Children's Privacy

CodeEditor is a general-purpose programming tool and does not knowingly collect or solicit personal information from children under the age of 13 (or the applicable age in your jurisdiction).

---

## 7. Changes to This Privacy Policy

We may update this Privacy Policy from time to time to reflect changes in our practices, features (such as adding ad networks), or applicable laws. When changes are made, we will update the "Last updated" date at the top of this policy and provide notice through application release notes or the project repository.

---

## 8. Contact Us

If you have questions, feedback, or concerns regarding this Privacy Policy or CodeEditor, please open an issue in the project repository or contact the project maintainers.
