# Use a lightweight Python base image
FROM python:3.11-slim

RUN apt-get update && apt-get install -y postgresql-client && rm -rf /var/lib/apt/lists/*

# Set the working directory inside the container
WORKDIR /app

# Copy the requirements file and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the import script 
COPY entrypoint.sh .
COPY import_data.py .

# The command to run when the container starts
RUN chmod +x entrypoint.sh
CMD ["./entrypoint.sh"]
# CMD ["python", "import_data.py"]
