## Sample code

- This `sample` folder contains various common coding practices that should be followed for coding consistency.
- There are 2 main business entities under `sample` folder:
    - `Equipment`: Represents an equipment that needs to be managed, such as a computer.
    - `MaintenanceRecord`: Represents a maintenance record created for an `Equipment`.
- The business stories contains:
    - Create an `Equipment`.
    - Update the `name` of an `Equipment`. The updated name should also be reflected in all `MaintenanceRecord`s for
      this
      `Equipment`,this is achieved using domain event.
    - Update the `holder` of an `Equipment`.
    - Delete an `Equipment`. This should also delete all `MaintenanceRecord`s for this `Equipment`, this is achieved
      using domain event.
    - Create a `MaintenanceRecord`. Its `status` will be used to update the `status` of the `Equipment`, this
      is achieved using domain event.