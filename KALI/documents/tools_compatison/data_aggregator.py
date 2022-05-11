import pandas as pd 


# This python script aggregates the data coming from the CT-Competition evaluator
if __name__ == "__main__":
    input_file_path = "best_results.csv"
    df_input = pd.read_csv(input_file_path, delimiter=",")
    df_output = pd.DataFrame(columns=["ModelName","TimeKali","SizeKali","TimePMedici", "SizePMedici", "TimeActs", "SizeActs"])
    df_output_no_timeouts = pd.DataFrame(columns=["ModelName","TimeKali","SizeKali","TimePMedici", "SizePMedici", "TimeActs", "SizeActs"])
    model_names = df_input["ModelName"].unique()
    strength = 2

    # Fetch all the models
    for model_name in model_names:
        size_kali = -1
        time_kali = -1
        size_pmedici = -1
        time_pmedici = -1
        size_acts = -1
        time_acts = -1

        # Get only the rows for the selected model
        pmedici_df = df_input[(df_input.ModelName.eq(model_name)) & (df_input.Strength.eq(strength)) & (df_input.ToolName.eq("pmedici"))]
        kali_df = df_input[(df_input.ModelName.eq(model_name)) & (df_input.Strength.eq(strength)) & (df_input.ToolName.eq("kali"))]
        acts_df = df_input[(df_input.ModelName.eq(model_name)) & (df_input.Strength.eq(strength)) & (df_input.ToolName.eq("acts"))]

        # Get the relevant data for all the generators
        size_kali = kali_df.Size.min()
        time_kali = kali_df.TimeSeconds.min()
        size_pmedici = pmedici_df.Size.min()
        time_pmedici = pmedici_df.TimeSeconds.min()
        size_acts = acts_df.Size.min()
        time_acts = acts_df.TimeSeconds.min()

        # If timeout, then set size and time to -1
        if kali_df.shape[0] > 0:
            if kali_df["ErrorType"].any() == "Timeout":
                size_kali = -1
                time_kali = -1

        if pmedici_df.shape[0] > 0:
            if pmedici_df["ErrorType"].any() == "Timeout":
                size_pmedici = -1
                time_pmedici = -1

        # Write the output dataframe
        df_output = df_output.append({
            "ModelName": model_name,
            "TimeKali": time_kali,
            "SizeKali": size_kali,
            "TimePMedici": time_pmedici,
            "SizePMedici": size_pmedici,
            "TimeActs": time_acts, 
            "SizeActs": size_acts
        }, ignore_index=True)

        # Write the output dataframe without df_output_no_timeouts
        if time_pmedici != -1 and time_kali != -1 and time_acts != -1:
            df_output_no_timeouts = df_output_no_timeouts.append({
                "ModelName": model_name,
                "TimeKali": time_kali,
                "SizeKali": size_kali,
                "TimePMedici": time_pmedici,
                "SizePMedici": size_pmedici,
                "TimeActs": time_acts, 
                "SizeActs": size_acts
            }, ignore_index=True)

    df_output.to_csv("aggregated_best.csv", index = False)
    df_output_no_timeouts.to_csv("aggregated_best_no_timeouts.csv", index = False)
