package com.example.singbike.Fragments.AccountTab;
/*
 * Users will be able to report any issues regarding the app, bicycles and inconvenience during the rides.
 * Upon Submit Button, the customer support team will receive an email from the user.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.AttachmentRecyclerViewAdapter;
import com.example.singbike.Fragments.HomeFragment;
import com.example.singbike.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;

public class ReportFragment extends Fragment {

    private RecyclerView attachmentRecyclerView;
    private AttachmentRecyclerViewAdapter attachmentRecyclerViewAdapter;

    public ReportFragment () { super(R.layout.fragment_report); }

    @Override
    public void onViewCreated (@NonNull View view, Bundle savedInstanceStates) {
        super.onViewCreated (view, savedInstanceStates);

        final ImageButton backButton = view.findViewById (R.id.backButton_ReportProblem);
        final EditText titleET = view.findViewById (R.id.reportTitleET);
        final EditText descriptionTextBox = view.findViewById (R.id.reportDescription);
        final Button addAttachmentButton = view.findViewById (R.id.addAttachmentButton_Report);
        final Button submitButton = view.findViewById (R.id.submitButton_Report);
        attachmentRecyclerView = view.findViewById (R.id.attachmentRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager (requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        attachmentRecyclerView.setLayoutManager (layoutManager);

        attachmentRecyclerViewAdapter = new AttachmentRecyclerViewAdapter (new AttachmentRecyclerViewAdapter.AttachmentOnClickListener() {
            @Override
            public void onClickAttachment(Uri imageUri, final int position) {
                /* on click action on attachment */
                AlertDialog.Builder builder = new AlertDialog.Builder (requireActivity());
                View v = LayoutInflater.from (requireActivity())
                        .inflate (R.layout.edit_attachment, null);
                builder.setTitle("Edit Attachment")
                        .setView (v)
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /* Remove Attachment */
                                attachmentRecyclerViewAdapter.removeAttachment (position);
                            }
                        })
                        .setNegativeButton ("Dismiss", new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick (DialogInterface dialogInterface, int which) {
                                /* Dismiss Action */
                                dialogInterface.dismiss();
                            }
                        });
                /* show attachment in ImageView */
                final ImageView attachmentView = v.findViewById (R.id.attachmentImage_Edit);
                attachmentView.setImageURI (imageUri);

                builder.create();
                builder.show();
            }
        });

        attachmentRecyclerView.setAdapter (attachmentRecyclerViewAdapter);

        /* add attachments to report */
        addAttachmentButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachmentRecyclerView.setVisibility (View.VISIBLE);
                    // ask user for the attachment source: Camera or Gallery
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                            requireActivity(),
                            R.style.BottomSheetDialogTheme
                    );
                    final View bottomSheet = LayoutInflater.from (requireActivity())
                            .inflate (R.layout.change_avatar_options, (LinearLayout) requireActivity().findViewById (R.id.changeAvatarSheet));

                    final Button cameraButton = bottomSheet.findViewById (R.id.openCameraButton_UploadAvatar);
                    final Button galleryButton = bottomSheet.findViewById (R.id.chooseGalleryButton_UploadAvatar);

                    // import attachment from the camera
                    cameraButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewCamera();
                                bottomSheetDialog.dismiss();
                            }
                        }
                    );

                    // import attachment from gallery
                    galleryButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setType ("image/*");
                                intent.setAction (Intent.ACTION_GET_CONTENT);
                                openGalleryLauncher.launch (intent);
                                bottomSheetDialog.dismiss();
                            }
                        }
                    );

                    bottomSheetDialog.setContentView (bottomSheet);
                    bottomSheetDialog.show();
                }
            }
        );

        /* submit a report */
        submitButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* Report must include title */
                    if (titleET.getText().toString().equals("")) {
                        titleET.requestFocus();
                        return;
                    }
                    /* Report Must Include Description */
                    if (descriptionTextBox.getText().toString().equals("")) {
                        descriptionTextBox.requestFocus();
                        return;
                    }

                    View loadingView = LayoutInflater.from (requireActivity())
                            .inflate (R.layout.loading, null);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

                    builder.setView (loadingView);
                    final TextView loadingText = loadingView.findViewById (R.id.loadingText);
                    loadingText.setText (R.string.submitting);

                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    // fake network loading
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 5000);
                }
            }
        );

        /* back button click */
        backButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    (requireActivity()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace (R.id.fragmentContainerView, HomeFragment.class, null)
                            .addToBackStack ("home")
                            .setReorderingAllowed (true)
                            .commit();
                }
            }
        );

    }

    /* get attachments (images) from the Camera */
    private void viewCamera () {
        if (ContextCompat.checkSelfPermission (requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // camera permission already granted
            try {
                Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch (intent);
            }
            catch (ActivityNotFoundException ane) {
                ane.printStackTrace();
            }
        }
        else if (shouldShowRequestPermissionRationale (Manifest.permission.CAMERA)) {
            // display something to explain why we need that permission
            final AlertDialog.Builder builder = new AlertDialog.Builder (requireActivity());
            builder.setTitle (R.string.camera_request)
                    .setPositiveButton (R.string.allow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .setNegativeButton(R.string.never, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create();
            builder.show();
        }
    }

    /* request images from the files (Device Storage) */
    ActivityResultLauncher<Intent> openGalleryLauncher = registerForActivityResult (new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult (ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            /* add new attachment to RecyclerView */
                            attachmentRecyclerViewAdapter.addAttachment (imageUri);
                            attachmentRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

    /* request images (attachments) from the camera */
    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult (new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult (ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap bitmap = (Bitmap) extras.get ("data");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        /* compress attachment (image) and convert to Uri String */
                        if (bitmap != null) {
                            bitmap.compress (Bitmap.CompressFormat.JPEG, 100, bytes);
                            String path = MediaStore.Images.Media.insertImage (requireActivity().getContentResolver(), bitmap, "title", null);
                            attachmentRecyclerViewAdapter.addAttachment (Uri.parse(path));
                            attachmentRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

}
